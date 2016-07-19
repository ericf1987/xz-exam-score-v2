package javalang;

public class TestStringFormat {

    public static void main(String[] args) {
        //System.out.println(String.format("%09d", 1));
//        TestStringFormat.Cubic cubic = new TestStringFormat.Cubic(10, 20, 30);
        TestStringFormat test = new TestStringFormat();
        TestStringFormat.Cubic cubic = test.new Cubic(1,2,3);
        System.out.println(cubic.Volumn());
    }


    class Cubic{
        int length;
        int width;
        int height;

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        Cubic(){
            super();
        }

        Cubic(int length, int width, int height){
            this.length = length;
            this.width = width;
            this.height = height;
        }

        public int Volumn(){
            return this.length * this.width * this.height;
        }
    }

}
