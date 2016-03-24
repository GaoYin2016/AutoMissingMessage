package dev.journey.auotodismiss.view;


import dev.journey.autodismiss.R;

public class GoodsInitUtile {

    private static int goodType1 = R.mipmap.live_im_good_type1;
    private static int goodType2 = R.mipmap.live_im_good_type2;
    private static int goodType3 = R.mipmap.live_im_good_type3;
    private static int goodType4 = R.mipmap.live_im_good_type4;
    private static int goodType5 = R.mipmap.live_im_good_type5;
    private static int goodType6 = R.mipmap.live_im_good_type6;
    private static int goodType7 = R.mipmap.live_im_good_type7;
    private static int goodType8 = R.mipmap.live_im_good_type8;
    private static int goodType9 = R.mipmap.live_im_good_type9;
    private static int goodType10 = R.mipmap.live_im_good_type10;
    private static int goodType11 = R.mipmap.live_im_good_type11;
    private static int goodType12 = R.mipmap.live_im_good_type12;
    private static int goodType13 = R.mipmap.live_im_good_type13;
    private static int goodType14 = R.mipmap.live_im_good_type14;

    private static int TYPECOUNT = 14;

    /*
     *   不同用户的标识id
     */
    public static int fromGoodID(int _id) {
        int rint = (int) (_id % TYPECOUNT);
        return rint;
    }

    /*
     * 获取点赞的类型
     */
    public static int getGoodsType(int type) {
        int rType = goodType1;
        switch (type) {
            case 1:
                rType = goodType1;
                break;
            case 2:
                rType = goodType2;
                break;
            case 3:
                rType = goodType3;
                break;
            case 4:
                rType = goodType4;
                break;
            case 5:
                rType = goodType5;
                break;
            case 6:
                rType = goodType6;
                break;
            case 7:
                rType = goodType7;
                break;
            case 8:
                rType = goodType8;
                break;
            case 9:
                rType = goodType9;
                break;
            case 10:
                rType = goodType10;
                break;
            case 11:
                rType = goodType11;
                break;
            case 12:
                rType = goodType12;
                break;
            case 13:
                rType = goodType13;
                break;
            case 14:
                rType = goodType14;
                break;
        }
        return rType;
    }

}
