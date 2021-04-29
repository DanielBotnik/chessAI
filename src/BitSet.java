
public class BitSet {

    public static String convertBoardToString(long board){
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder rowBuilder = new StringBuilder();
        for(int i = 0 ; i < 64 ; i++) {
            rowBuilder.append((board >> (63-i)) & 1);
            if(i % 8 == 7 ) {
                stringBuilder.append(rowBuilder.reverse()).append('\n');
                rowBuilder = new StringBuilder();
            }

        }
        return stringBuilder.toString();
    }
}
