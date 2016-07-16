package hc.teacher.utils;

import hc.teacher.application.R;

public class SubjectUtil
{
    /**
     * 根据科目id转换为对应科目
     * @param id    科目id
     * @return      对应科目中文名
     */
    public static String getSubject(int id)
    {
        switch(id)
        {
        case R.id.chinese_ll:
            return "语文";
        case R.id.math_ll:
            return "数学";
        case R.id.english_ll:
            return "英语";
        case R.id.physics_ll:
            return "物理";
        case R.id.chem_ll:
            return "化学";
        case R.id.biology_ll:
            return "生物";
        case R.id.geography_ll:
            return "地理";
        case R.id.history_ll:
            return "历史";
        case R.id.politics_ll:
            return "政治";
        default:
            return "其它";
        }
    }
}
