package cryp;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.math.BigInteger;

public class PallottiRSATextConverter {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter a string: ");
        String inputString = scan.nextLine();
        scan.close();

        /* RSA key generation
         * e: Public exponenent for encryption
         * d: Private exponent for encryption
         * n: Modulus for the e/d operations
        */
        KeyPair keyPair = keyGen();
        System.out.println("Public Key (e, n): " + keyPair.publicKey.exponent + ", " + keyPair.publicKey.modulus);
        System.out.println("Private Key (d, n): " + keyPair.privateKey.exponent + ", " + keyPair.privateKey.modulus);

        // convert string to list of characters
        List<Character> charList = stringToList(inputString);
        System.out.println("String to List: " + charList);

        // convert list of characters to ASCII codes
        List<Integer> asciiList = charToASCII(charList);
        System.out.println("List to ASCII: " + asciiList);

        // encrypt ASCII codes using RSA
        List<Integer> encryptedList = rsaEncrypt(asciiList, keyPair.publicKey);
        System.out.println("Encrypted List: " + encryptedList);

        // decrypt the encrypted list using RSA
        List<Integer> decryptedList = rsaDecrypt(encryptedList, keyPair.privateKey);
        System.out.println("Decrypted List: " + decryptedList);

        // convert decrypted ASCII codes to list of characters
        List<Character> charList2 = asciiToChar(decryptedList);
        System.out.println("ASCII to List: " + charList2);

        // convert list of characters back to string
        String outputString = listToString(charList2);
        System.out.println("List to String: " + outputString);
    }

    /* RSA Key Generation
    *p: A randomly generated prime number between 100 and 1000.
    q: Another randomly generated prime number between 100 and 1000, distinct from p.
    n: The product of p and q, used as the RSA modulus.
    phi: Euler's totient of n, calculated as (p - 1) * (q - 1).
    e: The public exponent, a coprime of phi.
    d: The private exponent, calculated as the multiplicative inverse of e modulo phi.
    */
    public static KeyPair keyGen() {
        // Generate two distinct prime numbers p and q
        int p = generatePrimeNumber(100, 1000);
        int q = generatePrimeNumber(100, 1000);

        int n = p * q;
        int phi = (p - 1) * (q - 1);
        int e = findCoprime(phi);
        int d = multiplicativeInverse(e, phi);

        PublicKey publicKey = new PublicKey(e, n);
        PrivateKey privateKey = new PrivateKey(d, n);

        return new KeyPair(publicKey, privateKey);
    }


    // function 1: String to List of characters
    public static List<Character> stringToList(String inputString) {
        List<Character> charList = new ArrayList<>();
        for (char c : inputString.toCharArray()) {
            charList.add(c);
        }
        return charList;
    }

    // function 2: List of characters to ASCII
    public static List<Integer> charToASCII(List<Character> charList) {
        List<Integer> asciiList = new ArrayList<>();
        for (char c : charList) {
            asciiList.add((int) c);
        }
        return asciiList;
    }

    // function 3: ASCII to Binary
    public static List<String> asciiToBinary(List<Integer> asciiList) {
        List<String> binaryList = new ArrayList<>();
        for (int ascii : asciiList) {
            binaryList.add(Integer.toBinaryString(ascii));
        }
        return binaryList;
    }

    // function 4: RSA Encryption
    public static List<Integer> rsaEncrypt(List<Integer> plaintext, PublicKey publicKey) {
        List<Integer> ciphertext = new ArrayList<>();
        for (int m : plaintext) {
            BigInteger bigM = BigInteger.valueOf(m);
            BigInteger modNum = BigInteger.valueOf(publicKey.modulus);
            BigInteger bigE = BigInteger.valueOf(publicKey.exponent);
            BigInteger encrypted = bigM.modPow(bigE, modNum);
            ciphertext.add(encrypted.intValue());
        }
        return ciphertext;
    }

    // function 5: RSA Decryption
    public static List<Integer> rsaDecrypt(List<Integer> ciphertext, PrivateKey privateKey) {
        List<Integer> plaintext = new ArrayList<>();
        for (int encrypted : ciphertext) {
            BigInteger dataNum = BigInteger.valueOf(encrypted);
            BigInteger modNum = BigInteger.valueOf(privateKey.modulus);
            BigInteger expNum = BigInteger.valueOf(privateKey.exponent);
            BigInteger decrypted = dataNum.modPow(expNum, modNum);
            plaintext.add(decrypted.intValue());
        }
        return plaintext;
    }

    // function 6: ASCII to List of characters
    public static List<Character> asciiToChar(List<Integer> asciiList) {
        List<Character> charList = new ArrayList<>();
        for (int ascii : asciiList) {
            char c = (char) ascii;
            charList.add(c);
        }
        return charList;
    }

    // function 7: List of characters to String
    public static String listToString(List<Character> charList) {
        StringBuilder sb = new StringBuilder();
        for (char c : charList) {
            sb.append(c);
        }
        return sb.toString();
    }

    // generate prime number between min and max (inclusive)
    public static int generatePrimeNumber(int min, int max) {
        int prime = 0;
        boolean isPrime = false;
        while (!isPrime) {
            prime = (int) (Math.random() * (max - min + 1) + min);
            isPrime = isPrime(prime);
        }
        return prime;
    }

    // check if a number is prime
    public static boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    // find a coprime number with phi
    public static int findCoprime(int phi) {
        for (int coprime = 2; coprime < phi; coprime++) {
            if (gcd(coprime, phi) == 1) {
                return coprime;
            }
        }
        return -1; // no coprime found
    }

    // calculate the greatest common divisor (gcd) of two numbers
    public static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

 // calculate the multiplicative inverse of a mod m
    public static int multiplicativeInverse(int a, int m) {
        int m2 = m;
        int y = 0, x = 1;

        if (m == 1) {
            return 0;
        }

        while (a > 1) {
            int quotient = a / m;
            int temp = m;
            m = a % m;
            a = temp;
            temp = y;
            y = x - quotient * y;
            x = temp;
        }

        if (x < 0) {
            x += m2;
        }

        return x;
    }


    static class KeyPair {
        PublicKey publicKey;
        PrivateKey privateKey;

        // represents a key pair consisting of a public key and a private key.
        public KeyPair(PublicKey publicKey, PrivateKey privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }
    }

    static class PublicKey {
        int exponent;
        int modulus;

        // represents a public key used for encryption.
        public PublicKey(int exponent, int modulus) {
            this.exponent = exponent;
            this.modulus = modulus;
        }
    }

    static class PrivateKey {
        int exponent;
        int modulus;

        // represents a private key used for decryption.
        public PrivateKey(int exponent, int modulus) {
            this.exponent = exponent;
            this.modulus = modulus;
        }
    }

}

