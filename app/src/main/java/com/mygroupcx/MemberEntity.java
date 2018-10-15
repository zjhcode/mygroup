package com.mygroupcx;

import android.support.annotation.IntDef;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
public class MemberEntity implements Serializable {
    @Id
    public long primaryKey;

    //序号
    @Index
    public long serialNumber;

    //昵称
    public String nickname;
    //姓名
    public String name;
    //性别（小哥哥：0 小姐姐：1）
    public int gender;
    //年龄（小鲜肉：0, 油腻中年：1, 老腊肉：2）
    public int age;
    //类型（0：普通成员 1：核心成员 2：管理员）
    public int type;
    //级别（0：萌新 1：凡人 2：神仙）
    public int level;
    //活跃度（0:低 1：中 2：高 3：敲级 ）
    public int liveness = 1;

    public String getGenderContent() {
        switch (gender) {
            case 0:
                return "♂";
            case 1:
                return "♀";
        }

        return null;
    }

    public String getAgeContent() {
        switch (age) {
            case 0:
                return "小鲜肉";
            case 1:
                return "油腻中年";
            case 2:
                return "老腊肉";
        }

        return null;
    }

    public String getTypeContent() {
        switch (type) {
            case 0:
                return "普通成员";
            case 1:
                return "核心成员";
            case 2:
                return "管理员";
        }

        return null;
    }

    public String getLevelContent() {
        switch (level) {
            case 0:
                return "萌新";
            case 1:
                return "凡人";
            case 2:
                return "神仙";
        }

        return null;
    }

    public String getLivenessContent() {
        switch (liveness) {
            case 0:
                return "低";
            case 1:
                return "中";
            case 2:
                return "高";
            case 3:
                return "敲级";
        }

        return null;
    }
}
