package com.shakazxx.couponspeeder.core.party;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommentDict {

    public static String pick() {
        List<String> comments = Arrays.asList("不忘初心，牢记使命，砥砺前行，奋进不止",
                "牢记初心和使命，坚持一心为民",
                "永远在学习的路上",
                "与人民同甘苦，与人民同进退",
                "中国人民有信心走好自己的路",
                "撸起袖子加油干，迈入小康社会",
                "早日实现中华民族伟大复兴",
                "实事求是，求真务实，空谈误国，实干兴邦");
        Random r = new Random();
        return comments.get(r.nextInt(comments.size()));
    }
}
