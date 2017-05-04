package javalang;

/**
 * (description)
 * created at 2017/5/4
 *
 * @author yidin
 */
public class DCLDemo {

    private String name;

    public String getName() {

        if (name == null) {
            synchronized (this) {
                if (name == null) {
                    name = new String();
                }
            }
        }

        return name;
    }
}
