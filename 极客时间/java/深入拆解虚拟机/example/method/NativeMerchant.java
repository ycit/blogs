import java.util.Random;

/**
 * 奸商
 *
 * @author chenxiaolei
 * @date 2020/7/25
 */
public class NativeMerchant extends Merchant {

    @Override
    public Double discount(double originalPrice, Customer customer) {
        if (customer.isVIP()) {
            return originalPrice*discriminationPrice();
        } else {
            return (Double) super.discount(originalPrice, customer);
        }
    }

    public static double discriminationPrice() {
        return new Random().nextDouble() + 0.8d;
    }
}