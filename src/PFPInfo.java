import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class PFPInfo {

    @JSONField(serialize = false)
    private String index;
    private String id;
    private String image;
    @JSONField(serialize = false)
    private String title;

    private String name;
    @JSONField(serialize = false)
    private String job;
    private String description;
    @JSONField(serialize = false)
    private String appellation;
    @JSONField(serialize = false)
    private String moniker;
    @JSONField(serialize = false)
    private String moniker_meaning;

    private String hero_image;

    public String getHero_image() {
        return hero_image;
    }

    public void setHero_image(String hero_image) {
        this.hero_image = hero_image;
    }

    private List<AttributeInfo> attributes;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getAppellation() {
        return appellation;
    }

    public void setAppellation(String appellation) {
        this.appellation = appellation;
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

    public List<AttributeInfo> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeInfo> attributes) {
        this.attributes = attributes;
    }
}
