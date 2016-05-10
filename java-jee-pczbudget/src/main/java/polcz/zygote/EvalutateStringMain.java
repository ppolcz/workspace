package polcz.zygote;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;

public class EvalutateStringMain
{
    public static void main(String[] args) throws Exception
    {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        String foo = "-40 + 2 + 2 * ( 10 + 21 )";
        System.out.print(foo + " = ");
        System.out.println(engine.eval(foo));
        System.out.println(engine.eval("2 * 6"));
    }
}