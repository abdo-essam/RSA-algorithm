import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;


public class RSA {

    public static void main(String[] args) {
 /*     System.out.print("Enter Plain Text : ");
        Scanner scanner = new Scanner(System.in);
        String plainText = scanner.nextLine();

 */

        String plainText = "TEST";

/*      final int BITS = 1024;
        final BigInteger p = generatePrime(BITS);
        final BigInteger q = generatePrime(BITS);
*/

        final BigInteger p = new BigInteger("173");
        final BigInteger q = new BigInteger("149");

        final BigInteger n = p.multiply(q);
        final BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        final BigInteger e = get_encryption_key(phi);
        final BigInteger d = get_decryption_key(e, phi);


//  ------------------------------------------------------------------------------------------
        System.out.println();
        System.out.println("p = " + p + ", q = " + q);
        System.out.println("n = p * q = " + p + " * " + q + " = " + n);
        System.out.println("φ(n) = " + phi);
        System.out.println("e = " + e);
        System.out.println("d = " + d);
        System.out.print("message = " + plainText + " , ");
        byte[] ASCIIValues = plainText.getBytes();

        for (int i = 0; i < plainText.length(); i++) {
            System.out.print((char) ASCIIValues[i] + " -> " + ASCIIValues[i] + "   ");
        }
        System.out.println();
        byte m;
        ArrayList<BigInteger> encrypted = new ArrayList<>();
        ArrayList<BigInteger> decrypted = new ArrayList<>();


        for (int i = 0; i < plainText.length(); i++) {
            System.out.println();
            m = ASCIIValues[i];
            System.out.println("For m = " + (char) ASCIIValues[i]);
            final BigInteger message = new BigInteger(String.valueOf(m)); // convert plain text (characters) to bytes()

            //final BigInteger encrypted_text = encrypt(e, message, n);
            //Square_and_Multiply
            final BigInteger encrypted_text = Square_and_Multiply(message, e, n);
            //final BigInteger decrypted_text = Square_and_Multiply(encrypted_text,d, n);
            //CRT(BigInteger d,BigInteger p,BigInteger q,ArrayList<BigInteger> encrypted,BigInteger n)
            final BigInteger decrypted_text = CRT(d, p, q, encrypted_text, n);
            System.out.println("The encryption of m = " + ASCIIValues[i] + " is c = " + ASCIIValues[i] + "^" + e + " % " + n + " = " + encrypted_text);
            System.out.println("The decryption of c = " + encrypted_text + " is m = " + encrypted_text + "^" + d + " % " + n + " = " + decrypted_text);
            encrypted.add(encrypted_text);
            decrypted.add(decrypted_text);
        }

        System.out.println();
        System.out.println("Message to encrypt: " + plainText);
        System.out.println("Message converted to ASCII code: " + Arrays.toString(plainText.getBytes()));

        System.out.println("Encrypted Message: " + Arrays.toString(encrypted.toArray()) + " (CipherText)");
        System.out.println("Message decrypted to ASCII code: " + Arrays.toString(decrypted.toArray()));
        System.out.print("Decrypted Message: ");

        for (int i = 0; i < decrypted.size(); i++) {
            System.out.print((char) Integer.parseInt(decrypted.get(i).toString()));
        }
        System.out.println();

    }

    private static BigInteger get_encryption_key(BigInteger phi) {
        BigInteger key = BigInteger.ZERO; // key = 0
        BigInteger counter = new BigInteger("2"); // counter = 2

        while (counter.compareTo(phi) < 0) { // while counter < phi
            if (gcd(phi, counter).equals(BigInteger.ONE)) {
                key = counter;
                break;
            }

            counter = counter.add(BigInteger.ONE); // counter++
        }

        return key;
    }

    private static BigInteger get_decryption_key(BigInteger e, BigInteger phi) {
        //return e.modInverse(phi);

        return Square_and_Multiply(e, phii(phi).subtract(BigInteger.ONE), phi);
    }

    /*private static BigInteger encrypt(BigInteger e, BigInteger plain, BigInteger n) {
        return plain.modPow(e, n);
    }*/

    /*private static BigInteger decrypt(BigInteger d, BigInteger cipher, BigInteger n) {
        return cipher.modPow(d, n);
    }*/

    private static BigInteger gcd(BigInteger a, BigInteger b) {
        BigInteger dividend = (a.compareTo(b) >= 0) ? a : b; // if a > b  return a else b
        BigInteger divisor = (a.compareTo(b) <= 0) ? a : b;  // if a < b  return a else b


        while (!divisor.equals(BigInteger.ZERO)) {
            BigInteger remainder = dividend.mod(divisor);

            dividend = divisor;
            divisor = remainder;
        }

        return dividend;
    }

    private static BigInteger generatePrime(int bits) {
        SecureRandom ran = new SecureRandom();
        BigInteger prime = new BigInteger(bits, ran);

        while (true) {
            if (prime.isProbablePrime(1)) {
                break;
            }

            prime = prime.subtract(new BigInteger("1"));
        }

        return prime;
    }

    public static BigInteger Square_and_Multiply(BigInteger x, BigInteger H, BigInteger n) {
        String t = H.toString(2);
        //String t=BigInteger.toBinaryString(H);
        BigInteger r = x;
        for (int i = 1; i < t.length(); i++) {
            r = (r.multiply(r)).mod(n);
            if (t.charAt(i) == '1') {
                r = (r.multiply(x)).mod(n);
            }
        }
        return r;
    }

    /////////////////////////////
    public static BigInteger phii(BigInteger n) {
        BigInteger result = new BigInteger("1");


        for (BigInteger i = new BigInteger("2");
             i.compareTo(n) < 0;
             i = i.add(BigInteger.ONE)) {
            if (gcd(i, n).equals(BigInteger.ONE)) {
                result = result.add(BigInteger.ONE);

            }
        }
        return result;
    }

    ///////////////////////
    public static BigInteger CRT(BigInteger d, BigInteger p, BigInteger q, BigInteger encrypted, BigInteger n)//d=16971  ,q=149  ,p=173,n=25456
    {
        //int dq=d%(q-1);
        BigInteger dq = d.mod(q.subtract(BigInteger.ONE));
        //int dp=d%(p-1);
        BigInteger dp = d.mod(p.subtract(BigInteger.ONE));
        //int cp=Square_and_Multiply(q,phi(p)-1,p);
        BigInteger cp = Square_and_Multiply(q, phii(p).subtract(BigInteger.ONE), p);
        //int cq=Square_and_Multiply(p,phi(q)-1,q);
        BigInteger cq = Square_and_Multiply(p, phii(q).subtract(BigInteger.ONE), q);


        System.out.println("\n" + "The encryption of c =" + encrypted);
        System.out.println("dp = " + dp);
        System.out.println("dq = " + dq);

        BigInteger xp = encrypted.mod(p);
        System.out.println("xp = " + xp);

        BigInteger xq = encrypted.mod(q);
        System.out.println("xq = " + xq);

        //int yp=Square_and_Multiply(xp,dp,p);
        BigInteger yp = Square_and_Multiply(xp, dp, p);
        System.out.println("yp = " + yp);

        //int yq=Square_and_Multiply(xq,dq,q);
        BigInteger yq = Square_and_Multiply(xq, dq, q);
        System.out.println("yq = " + yq);

        System.out.println("cp = " + cp);
        System.out.println("cq = " + cq);

        //int res=((qcp)yp+(pcq)yq);
        BigInteger res = q.multiply(cp).multiply(yp).add(p.multiply(cq).multiply(yq));
        //int y =res%n;
        BigInteger y = res.mod(n);
        System.out.println("y = " + y);

        return y;
    }


}