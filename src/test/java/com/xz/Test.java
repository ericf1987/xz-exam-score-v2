package com.xz;

/**
 * (description)
 * created at 16/06/06
 *
 * @author yiding_he
 */
public class Test {

    public static void main(String[] args) {

/*        List<Teacher> teachers = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        List<Teacher> nonStudentTeachers = new ArrayList<>(teachers);

        Predicate<Teacher> isMarked = t -> {
            Predicate<Student> contains = _t -> t.getId().equals(_t.getId());
            return students.stream().anyMatch(contains);
        };

        nonStudentTeachers.removeIf(isMarked);

        Map<String, Teacher> teacherMap = new HashMap<>();*/
/*        System.out.println(Math.floor(13.75));
        System.out.println((int)Math.ceil(13.75));
        System.out.println(Double.valueOf(13.75).intValue());
        System.out.println((double)11 / 2.1);*/
/*        boolean test = true;
        System.out.println(test(test));
        System.out.println(test);*/
/*        StringBuilder builder = new StringBuilder("Hello");
        StringBuilder builder2 = test1(builder);
        System.out.println(builder.toString());*/

/*        String a = "Hello";
        String b = test2(a);
        System.out.println(a);*/

        Swapper swapper = new Swapper();

        String a = "Mightly";
        String b = "Probally";
        swapper.swapStr(a, b);
        System.out.println(a + "-->" +b);

        String[] s = new String[]{a, b};
        swapper.swapStr(s);
        for (String arr : s){
            System.out.println("item-->" + arr);
        }
    }

/*    static String test2(String a) {
        a = a.substring(3);
        return a;
    }

    static StringBuilder test1(StringBuilder builder) {
        return builder.append("China");
    }

    static boolean test(boolean b){
        b = !b;
        return b;
    }*/

    public static class Swapper<T> {
        public <T> void swapStr(T a, T b){
            T temp = a;
            a = b;
            b = temp;
        };

        public <T> void swapStr(T[] t){
            if(t.length < 2){
                System.out.println("error");
                return;
            }

            T temp = t[0];
            t[0] = t[1];
            t[1] = temp;
        }
    }

}
