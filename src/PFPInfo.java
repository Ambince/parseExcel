import com.alibaba.fastjson.annotation.JSONField;

public class PFPInfo {

    @JSONField(serialize = false)
    private String index;
    private String id;
    private String image_preview_url;
    private String title;
    private String name;
    private String moniker;
    private String moniker_meaning;
    private String faith;
    private String hierarchy;
    private String race;
    private String force;
    private String realm;
    private String type;
    private String job;
    private String size;
    private String gender;
    private String age;
    private String tale;
    private String note;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_preview_url() {
        return image_preview_url;
    }

    public void setImage_preview_url(String image_preview_url) {
        this.image_preview_url = image_preview_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMoniker() {
        return moniker;
    }

    public void setMoniker(String moniker) {
        this.moniker = moniker;
    }

    public String getMoniker_meaning() {
        return moniker_meaning;
    }

    public void setMoniker_meaning(String moniker_meaning) {
        this.moniker_meaning = moniker_meaning;
    }

    public String getFaith() {
        return faith;
    }

    public void setFaith(String faith) {
        this.faith = faith;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getForce() {
        return force;
    }

    public void setForce(String force) {
        this.force = force;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getTale() {
        return tale;
    }

    public void setTale(String tale) {
        this.tale = tale;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
