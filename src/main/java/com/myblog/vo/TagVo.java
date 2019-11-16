package com.myblog.vo;

import java.util.Objects;

/**
 *      前台显示标签对象
 */
public class TagVo {


    //标签名
    private String tagname;

    //标签数量
    private Integer count;


    //=========================================================
    public TagVo() {
    }

    public TagVo(String tagname, Integer count) {
        this.tagname = tagname;
        this.count = count;
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "TagVo{" +
                "tagname='" + tagname + '\'' +
                ", count=" + count +
                '}';
    }



    //


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagVo tagVo = (TagVo) o;
        return tagname.equals(tagVo.tagname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagname);
    }
}
