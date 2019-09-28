package com.mll.mll_project.visitor;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

public class Visitor {
    public static final String ID_FIELD = "id";
    public static final String CARDID_FIELD = "cardid";
    public static final String CODE_FIELD = "code";
    public static final String TYPE_FIELD = "type";
    public static final String NATION_FIELD = "nation";
    public static final String NAME_FIELD = "name";
    public static final String PHOTO_FIELD = "photo";
    public static final String DEPARTMENT_FIELD = "department";
    public static final String GENDER_FIELD = "gender";
    public static final String VALIDITYFROM_FIELD = "validityfrom";
    public static final String VALIDITYTO_FIELD = "validityto";
    public static final String ADDRESS_FIELD = "address";
    public static final String BIRTHDAY_FIELD = "birthday";
    public static final String CARDTIMESTAMP_FIELD = "cardtime";
    public static final String STATE_FIELD = "state";

    @DatabaseField(generatedId = true, columnName = ID_FIELD, allowGeneratedIdInsert = true)
    private Integer id;
    @DatabaseField(columnName = CARDID_FIELD, index = true)
    private String cardID;
    @DatabaseField(columnName = CODE_FIELD)
    private String code;
    @DatabaseField(columnName = TYPE_FIELD, index = true)
    private Integer type;
    @DatabaseField(columnName = NATION_FIELD)
    private String nation;
    @DatabaseField(columnName = NAME_FIELD, index = true)
    private String name;
    @DatabaseField(columnName = PHOTO_FIELD)
    private String photo;
    @DatabaseField(columnName = DEPARTMENT_FIELD)
    private String department;
    @DatabaseField(columnName = GENDER_FIELD)
    private int gender;
    @DatabaseField(columnName = VALIDITYFROM_FIELD, format = "yyyy-MM-dd")
    private Date validityFrom;
    @DatabaseField(columnName = VALIDITYTO_FIELD)
    private Date validityTo;
    @DatabaseField(columnName = ADDRESS_FIELD, format = "yyyy-MM-dd")
    private String address;
    @DatabaseField(columnName = BIRTHDAY_FIELD, format = "yyyy-MM-dd")
    private Date birthday;
    @DatabaseField(columnName = CARDTIMESTAMP_FIELD)
    private Date cardTime;
    @DatabaseField(columnName = STATE_FIELD, defaultValue = "0")
    private Integer state;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer tag) {
        this.state = tag;
    }

    public String getCardid() {
        return cardID;
    }

    public void setCardid(String cardID) {
        this.cardID = cardID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * true 女
     * false 男
     *
     * @return
     */
    public int getGender() {
        return gender;
    }

    /**
     * true 女
     * false 男
     *
     * @return
     */
    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getValidityTo() {
        return validityTo;
    }

    public void setValidityTo(Date validityTo) {
        this.validityTo = validityTo;
    }

    public Date getValidityFrom() {
        return validityFrom;
    }

    public void setValidityFrom(Date validityFrom) {
        this.validityFrom = validityFrom;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getCardTime() {
        return cardTime;
    }

    public void setCardTime(Date cardTime) {
        this.cardTime = cardTime;
    }
}
