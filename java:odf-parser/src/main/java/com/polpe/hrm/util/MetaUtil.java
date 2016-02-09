package com.polpe.hrm.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.polpe.hrm.db.AbstractDataStore;
import com.polpe.hrm.db.AbstractQuery;
import com.polpe.hrm.db.EColumnNames;
import com.polpe.hrm.db.ETableNames;
import com.polpe.hrm.db.ISQLCommands;
import com.polpe.hrm.db.ISQLiteHelper;

/**
 * Class to generate PlantUML descriptions from the declared classes
 * using methods and fields with arbitrary visibility option.
 * 
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Oct 17, 2014 8:48:14 PM
 */
public class MetaUtil
{
    public static String ParseGenericParamTypes(Type[] types)
    {
        String function_params = "";
        for (int i = 0; i < types.length; ++i)
        {
            String generic_part = "";
            String simple_type_part = "";
            String type_suf = "";
            try
            {
                String[] gensplit = types[i].toString().split("[<>]+");
                String[] generics = gensplit[1].split("[, ]+");
                simple_type_part = gensplit[0];
                for (String g : generics)
                {
                    String[] tmp = g.split("[. ]+");
                    generic_part += ", " + tmp[tmp.length - 1];
                }
                generic_part = "<" + generic_part.substring(2) + ">";
            }
            catch (IndexOutOfBoundsException e)
            {
                simple_type_part = types[i].toString();
            }

            String[] name = simple_type_part.split("[. ]+");
            String typename = name[name.length - 1];

            if (simple_type_part.contains("[L"))
            {
                type_suf += "[]";
                typename = typename.substring(0, typename.length() - 1);
            }

            function_params += ", " + typename + generic_part + type_suf;
        }
        try
        {
            function_params = "(" + function_params.substring(2) + ")";
        }
        catch (StringIndexOutOfBoundsException e)
        {
            function_params = "()";
        }
        return function_params;
    }

    public static String ModifierToPlantUML(int mod)
    {
        String pref = "";
        if (Modifier.isPrivate(mod)) pref += "- ";
        else if (Modifier.isPublic(mod)) pref += "+ ";
        else if (Modifier.isProtected(mod)) pref += "# ";
        else pref += "~ ";
        if (Modifier.isAbstract(mod)) pref += "{abstract} ";
        if (Modifier.isStatic(mod)) pref += "{static} ";

        return pref;
    }

    public static void GeneratePlantUML(Class<?> targetClass)
    {
        String classType = "class";

        if (targetClass.isInterface()) classType = "interface";
        if (targetClass.isEnum()) classType = "enum";

        /* array of class associations */
        List<String> links = new ArrayList<String>();

        String[] classNameSplit = targetClass.getCanonicalName().split("[. ]+");
        String className = classNameSplit[classNameSplit.length - 1];

        /**
         * Detect implementations
         */
        for (Class<?> intf : targetClass.getInterfaces())
        {
            String[] intfNameSplit = intf.getCanonicalName().split("[. ]+");
            String intfName = intfNameSplit[intfNameSplit.length - 1];
            links.add(className + " ..|> " + intfName);
        }

        /**
         * Detect parent class if
         *  - is not an interface
         *  - is not an enum
         *  - its super-class is not the Object
         */
        if (!targetClass.isInterface() && !targetClass.isEnum() && targetClass.getSuperclass() != Object.class)
        {
            String[] superClassNameSplit = targetClass.getSuperclass().getCanonicalName().split("[. ]+");
            String superClassName = superClassNameSplit[superClassNameSplit.length - 1];
            links.add(className + " --|> " + superClassName);
        }

        System.out.println(classType + " " + className + " {");

        /**
         * Go through the fields of the class
         */
        for (Field f : targetClass.getDeclaredFields())
        {
            /* enumeration constants */
            if (f.isEnumConstant())
            {
                /* print enum description */
                System.out.println("\t" + f.getName());
                continue;
            }

            /**
             * Get name of the type of the field:
             * java.lang.String --> String
             */
            String[] typenameSplit = f.getType().getName().split("[.$ ]+");
            String typename = typenameSplit[typenameSplit.length - 1];

            /**
             * Detect inner classes
             */
            if (f.getName().startsWith("this$"))
            {
                /* is inner class */
                if (Modifier.isStatic(f.getModifiers()))
                {
                    /* is static */
                    links.add(typename /* this is the container class */ + " \"inner\" +.. " + className);
                }
                else
                {
                    /* not static */
                    links.add(typename /* this is the container class */ + " \"inner\" +-- " + className);
                }
            }

            else
            {
                /**
                 * Detect aggregation
                 */
                if (f.getType().getName().startsWith("com.napol"))
                {
                    links.add(className + " \"" + f.getName() + "\" o-- " + typename);
                }

                /* print field description */
                System.out.println("\t" + ModifierToPlantUML(f.getModifiers()) + f.getName() + " : " + typename);
            }
        }

        System.out.println("\t--");

        /**
         * Go through constructors
         */
        for (Constructor<?> c : targetClass.getConstructors())
        {
            c.getGenericParameterTypes();
            System.out.println("\t" + ModifierToPlantUML(c.getModifiers()) + className +
                ParseGenericParamTypes(c.getGenericParameterTypes()));
        }

        /**
         * Go through declared methods
         */
        for (Method m : targetClass.getDeclaredMethods())
        {
            /**
             * TODO - what are these access methods? 
             * Are these some significant informations about the class?
             */
            if (m.getName().contains("access$")) continue;

            String[] returnTypeSplit = m.getGenericReturnType().toString().split("[. ]+");
            String returnType = returnTypeSplit[returnTypeSplit.length - 1];

            /**
             * TODO - is it a good idea?
             * Detect instantiation: if a method returns an instance of returnType,
             * it could be an instantiation association.
             */
            // System.out.println(m.getGenericReturnType().toString());
            // if (m.getGenericReturnType().toString().startsWith("class com.napol"))
            // {
            // links.add(className + " ..> " + returnType);
            // }

            System.out.println("\t" + ModifierToPlantUML(m.getModifiers()) + m.getName() +
                ParseGenericParamTypes(m.getGenericParameterTypes()) + " : " + returnType);
        }
        /* End of class */
        System.out.println("}\n");

        /* Print additional associations */
        for (String l : links)
            System.out.println(l);
        System.out.println("");
    }

    public static void main(String[] args)
    {
        GeneratePlantUML(AbstractDataStore.class);
        GeneratePlantUML(AbstractQuery.class);
        GeneratePlantUML(EColumnNames.class);
        GeneratePlantUML(ETableNames.class);
        GeneratePlantUML(ISQLCommands.class);
        GeneratePlantUML(ISQLiteHelper.class);
    }
}
