/**
 * Created by songll on 2017/4/25.
 */
public class StringToAscii {

    private static String toHexUtil(int n){
        String rt="";
        switch(n){
            case 10:rt+="A";break;
            case 11:rt+="B";break;
            case 12:rt+="C";break;
            case 13:rt+="D";break;
            case 14:rt+="E";break;
            case 15:rt+="F";break;
            default:
                rt+=n;
        }
        return rt;
    }

    public static String toHex(int n){
        StringBuilder sb=new StringBuilder();
        if(n/16==0){
            return toHexUtil(n);
        }else{
            String t=toHex(n/16);
            int nn=n%16;
            sb.append(t).append(toHexUtil(nn));
        }
        return sb.toString();
    }

    public static String parseAscii(String str){
        StringBuilder sb=new StringBuilder();
        byte[] bs=str.getBytes();
        for(int i=0;i<bs.length;i++)
            sb.append(toHex(bs[i]));
        return sb.toString();
    }

    public static void main(String args[]){
        //String s="xyz";
        String s="《天:王;盖<地>虎》天@王#盖$地%虎^宝&塔*镇~河|妖\\宝/塔+镇-河'妖\"宝\"塔镇河妖";
        System.out.println("转换后的字符串是："+StringToAscii.parseAscii(s));
    }
}