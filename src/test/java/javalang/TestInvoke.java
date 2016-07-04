package javalang;

/**
 * @author by fengye on 2016/6/30.
 */
public class TestInvoke {

    class Box{
        int length;
        int width;
        int height;

        Box(){};

        Box(int length, int width, int height){
            this.length = length;
            this.width = width;
            this.height = height;
        }
    }

    interface BoxFactory<B extends Box>{
        B creat(int length, int width, int height);
    }


    public static void main(String[] args) {
//        BoxFactory<Box> factory = Box::Box;
        String s = "true";
        System.out.println(Boolean.valueOf(s).booleanValue());
    }
}
