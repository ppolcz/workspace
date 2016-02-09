package polcz.zygote;

public class HiddenFieldsMain
{
    class Base
    {
        private int field = 1;
        
        public int getField()
        {
            return field;
        }
        
        public void setField(int field)
        {
            this.field = field;
        }
    }

    class Child extends Base
    {
        private int field = 2;
    }

    public HiddenFieldsMain()
    {
        Base a = new Child();
        System.out.println(a.getField());
    }
    
    public static void main(String[] args)
    {
        new HiddenFieldsMain();
    }

}
