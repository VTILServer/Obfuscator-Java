package org.luaj.vm2.compiler;

import icu.Xell.obfuscator.impl.CompilerOptions;
import org.luaj.vm2.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class XellLuaCompiler extends Lua implements XellLoadState.LuaCompiler {

    public static final XellLuaCompiler instance = new XellLuaCompiler();
    public static final int MAXSTACK = 250;
    static final int LUAI_MAXUPVALUES = 60;
    static final int LUAI_MAXVARS = 200;
    static final int NO_REG = MAXARG_A;
    /* OpMode - basic instruction format */
    static final int
            iABC = 0,
            iABx = 1,
            iAsBx = 2;
    /* OpArgMask */
    static final int
            OpArgN = 0,  /* argument is not used */
            OpArgU = 1,  /* argument is used */
            OpArgR = 2,  /* argument is a register or a jump offset */
            OpArgK = 3;   /* argument is a constant or register/constant */
    public int nCcalls;
    Hashtable strings;


    protected XellLuaCompiler() {
    }

    private XellLuaCompiler(Hashtable strings) {
        this.strings = strings;
    }

    /**
     * Install the compiler so that LoadState will first
     * try to use it when handed bytes that are
     * not already a compiled lua chunk.
     */
    public static void install() {
        XellLoadState.compiler = instance;
    }

    protected static void _assert(boolean b) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement element = stackTrace[2];
        //System.out.println("I was called by a method named: " + element.getMethodName());
        //System.out.println("That method is in class: " + element.getClassName());
        if (!b)
            throw new LuaError("compiler assert failed");
    }

    static void SET_OPCODE(InstructionPtr i, int o) {
        i.set((i.get() & (MASK_NOT_OP)) | ((o << POS_OP) & MASK_OP));
    }

    static void SETARG_A(InstructionPtr i, int u) {
        i.set((i.get() & (MASK_NOT_A)) | ((u << POS_A) & MASK_A));
    }

    static void SETARG_B(InstructionPtr i, int u) {
        i.set((i.get() & (MASK_NOT_B)) | ((u << POS_B) & MASK_B));
    }

    static void SETARG_C(InstructionPtr i, int u) {
        i.set((i.get() & (MASK_NOT_C)) | ((u << POS_C) & MASK_C));
    }

    // vector reallocation

    static void SETARG_Bx(InstructionPtr i, int u) {
        i.set((i.get() & (MASK_NOT_Bx)) | ((u << POS_Bx) & MASK_Bx));
    }

    static void SETARG_sBx(InstructionPtr i, int u) {
        SETARG_Bx(i, u + MAXARG_sBx);
    }

    static int CREATE_ABC(int o, int a, int b, int c) {
        return ((o << POS_OP) & MASK_OP) |
                ((a << POS_A) & MASK_A) |
                ((b << POS_B) & MASK_B) |
                ((c << POS_C) & MASK_C);
    }

    static int CREATE_ABx(int o, int a, int bc) {
        return ((o << POS_OP) & MASK_OP) |
                ((a << POS_A) & MASK_A) |
                ((bc << POS_Bx) & MASK_Bx);
    }

    static LuaValue[] realloc(LuaValue[] v, int n) {
        LuaValue[] a = new LuaValue[n];
        if (v != null)
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        return a;
    }

    static Prototype[] realloc(Prototype[] v, int n) {
        Prototype[] a = new Prototype[n];
        if (v != null)
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        return a;
    }

    static LuaString[] realloc(LuaString[] v, int n) {
        LuaString[] a = new LuaString[n];
        if (v != null)
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        return a;
    }

    static LocVars[] realloc(LocVars[] v, int n) {
        LocVars[] a = new LocVars[n];
        if (v != null)
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        return a;
    }

    static int[] realloc(int[] v, int n) {
        int[] a = new int[n];
        if (v != null)
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        return a;
    }

    static byte[] realloc(byte[] v, int n) {
        byte[] a = new byte[n];
        if (v != null)
            System.arraycopy(v, 0, a, 0, Math.min(v.length, n));
        return a;
    }

    /**
     * Compile a prototype or load as a binary chunk
     */
    public static Prototype compile(InputStream stream, String name, CompilerOptions opts) throws IOException {
        int firstByte = stream.read();
        return (firstByte == '\033') ?
                XellLoadState.loadBinaryChunk(firstByte, stream, name) :
                (new XellLuaCompiler(new Hashtable())).luaY_parser(firstByte, stream, name, opts);
    }

    /**
     * Load into a Closure or LuaFunction, with the supplied initial environment
     */
    public LuaFunction load(InputStream stream, String name, LuaValue env, CompilerOptions opts) throws IOException {
        Prototype p = compile(stream, name, opts);
        return new LuaClosure(p, env);
    }

    /**
     * Parse the input
     */
    private Prototype luaY_parser(int firstByte, InputStream z, String name, CompilerOptions opts) {
        XellLexState lexstate = new XellLexState(this, z, opts);
        XellFuncState funcstate = new XellFuncState(opts);
        // lexstate.buff = buff;
        lexstate.setinput(this, firstByte, z, (LuaString) LuaValue.valueOf(name));
        lexstate.open_func(funcstate);
        /* main func. is always vararg */
        funcstate.f.is_vararg = XellLuaCompiler.VARARG_ISVARARG;
        funcstate.f.source = (LuaString) LuaValue.valueOf(name);
        lexstate.next(); /* read first token */
        lexstate.chunk();
        lexstate.check(XellLexState.TK_EOS);
        lexstate.close_func();
        XellLuaCompiler._assert(funcstate.prev == null);
        XellLuaCompiler._assert(funcstate.f.nups == 0);
        XellLuaCompiler._assert(lexstate.fs == null);
        return funcstate.f;
    }

    // look up and keep at most one copy of each string
    public LuaString newTString(byte[] bytes, int offset, int len) {
        LuaString tmp = LuaString.valueOf(bytes, offset, len);
        LuaString v = (LuaString) strings.get(tmp);
        if (v == null) {
            // must copy bytes, since bytes could be from reusable buffer
            byte[] copy = new byte[len];
            System.arraycopy(bytes, offset, copy, 0, len);
            v = LuaString.valueOf(copy);
            strings.put(v, v);
        }
        return v;
    }

    public String pushfstring(String string) {
        return string;
    }

    public LuaFunction load(Prototype p, String filename, LuaValue env) {
        return new LuaClosure(p, env);
    }

}
