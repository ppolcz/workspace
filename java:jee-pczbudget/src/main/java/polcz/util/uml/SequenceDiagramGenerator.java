package polcz.util.uml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SequenceDiagramGenerator
{
    private static String getSimpleName(String name)
    {
        String[] tokens = name.split("[. ]+");
        return tokens[tokens.length - 1];
    }

    private static String getSimpleClassName(StackTraceElement e)
    {
        return getSimpleName(e.getClassName());
    }

    private static String getSimpleMethodName(StackTraceElement e)
    {
        if (e.getMethodName().startsWith("<init>")) return "new " + getSimpleClassName(e) + "()";
        return e.getMethodName() + "()";
    }

    private static class SequenceDiagram
    {
        Map<String, Boolean> active = new HashMap<String, Boolean>();
        List<String> plant = new ArrayList<String>();

        private void activate(String name)
        {
            // if (!active.get(name))
            // {
            // String last = plant.isEmpty() ? "" : plant.get(plant.size() - 1);
            // if (last.equals("activate " + name) || last.equals("deactivate "
            // + name))
            // {
            // plant.remove(plant.size() - 1);
            // }
            // active.remove(name);
            // active.put(name, true);
            plant.add("activate " + name);
            // }
        }

        private void deactivate(String name)
        {
            // String last = plant.get(plant.size() - 1);
            // if (active.get(name))
            // {
            // if (last.equals("activate " + name) || last.equals("deactivate "
            // + name))
            // {
            // plant.remove(plant.size() - 1);
            // }
            // active.remove(name);
            // active.put(name, false);
            plant.add("deactivate " + name);
            // }
        }
    }

    public static void plantSequencePolpe(Exception e)
    {
        SequenceDiagram sq = new SequenceDiagram();

        e.printStackTrace();
        StackTraceElement[] tre = e.getStackTrace();

        for (StackTraceElement t : tre)
        {
            sq.active.put(getSimpleClassName(t), false);
        }

        sq.plant.add("[--> " + getSimpleClassName(tre[tre.length - 1]) + " : start");
        sq.activate(getSimpleClassName(tre[tre.length - 1]));
        for (int i = tre.length - 1; i > 0; --i)
        {
            sq.plant.add(getSimpleClassName(tre[i]) + " --> " +
                getSimpleClassName(tre[i - 1]) + " : " + getSimpleMethodName(tre[i - 1]));
            if (!tre[i].getClassName().equals(tre[i - 1].getClassName()))
                sq.deactivate(getSimpleClassName(tre[i]));
            sq.activate(getSimpleClassName(tre[i - 1]));
        }
        for (int i = 1; i < tre.length; ++i)
        {
            sq.plant.add(getSimpleClassName(tre[i]) + " <-- " +
                getSimpleClassName(tre[i - 1]) + " : return");
            sq.deactivate(getSimpleClassName(tre[i - 1]));
            if (!tre[i].getClassName().equals(tre[i - 1].getClassName()))
                sq.activate(getSimpleClassName(tre[i]));
        }
        sq.plant.add("[<--" + getSimpleClassName(tre[tre.length - 1]) + " : end");
        sq.deactivate(getSimpleClassName(tre[tre.length - 1]));

        for (String line : sq.plant)
        {
            System.out.println(line);
        }
    }

    public static void plantSequence(Exception e)
    {
        SequenceDiagram sq = new SequenceDiagram();

        StackTraceElement[] tre = e.getStackTrace();

        for (StackTraceElement t : tre)
        {
            sq.active.put(getSimpleClassName(t), false);
        }

        sq.plant.add("[--> " + getSimpleClassName(tre[tre.length - 1]) + " : start");
        sq.activate(getSimpleClassName(tre[tre.length - 1]));
        for (int i = tre.length - 1; i > 0; --i)
        {
            sq.plant.add(getSimpleClassName(tre[i]) + " --> " +
                getSimpleClassName(tre[i - 1]) + " : " + getSimpleMethodName(tre[i - 1]));
            if (!tre[i].getClassName().equals(tre[i - 1].getClassName()))
                sq.deactivate(getSimpleClassName(tre[i]));
            sq.activate(getSimpleClassName(tre[i - 1]));
        }
        for (int i = 1; i < tre.length; ++i)
        {
            // sq.plant.add(getSimpleClassName(tre[i]) + " <-- " +
            // getSimpleClassName(tre[i - 1]) + " : return");
            sq.deactivate(getSimpleClassName(tre[i - 1]));
            if (!tre[i].getClassName().equals(tre[i - 1].getClassName()))
                sq.activate(getSimpleClassName(tre[i]));
        }
        // sq.plant.add("[<--" + getSimpleClassName(tre[tre.length - 1]) + " :
        // end");
        sq.deactivate(getSimpleClassName(tre[tre.length - 1]));

        for (String line : sq.plant)
        {
            System.out.println(line);
        }
    }
}
