package Entity;

import javax.persistence.*;

@Entity
@Table(name = "task", schema = "lowcostfuel", catalog = "")
public class TaskEntity {
    private int id;
    private int userId;
    private Integer pulls95;
    private Integer a95Euro;
    private Integer a92Euro;
    private Integer dieseleuro;
    private Integer pullsdiesel;
    private Integer lpg;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "user_id", nullable = false, unique = true)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "pulls95", nullable = true)
    public Integer getPulls95() {
        return pulls95;
    }

    public void setPulls95(Integer pulls95) {
        this.pulls95 = pulls95;
    }

    @Basic
    @Column(name = "a95euro", nullable = true)
    public Integer getA95Euro() {
        return a95Euro;
    }

    public void setA95Euro(Integer a95Euro) {
        this.a95Euro = a95Euro;
    }

    @Basic
    @Column(name = "a92euro", nullable = true)
    public Integer getA92Euro() {
        return a92Euro;
    }

    public void setA92Euro(Integer a92Euro) {
        this.a92Euro = a92Euro;
    }

    @Basic
    @Column(name = "dieseleuro", nullable = true)
    public Integer getDieseleuro() {
        return dieseleuro;
    }

    public void setDieseleuro(Integer dieseleuro) {
        this.dieseleuro = dieseleuro;
    }

    @Basic
    @Column(name = "pullsdiesel", nullable = true)
    public Integer getPullsdiesel() {
        return pullsdiesel;
    }

    public void setPullsdiesel(Integer pullsdiesel) {
        this.pullsdiesel = pullsdiesel;
    }

    @Basic
    @Column(name = "lpg", nullable = true)
    public Integer getLpg() {
        return lpg;
    }

    public void setLpg(Integer lpg) {
        this.lpg = lpg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskEntity that = (TaskEntity) o;

        if (id != that.id) return false;
        if (userId != that.userId) return false;
        if (pulls95 != null ? !pulls95.equals(that.pulls95) : that.pulls95 != null) return false;
        if (a95Euro != null ? !a95Euro.equals(that.a95Euro) : that.a95Euro != null) return false;
        if (a92Euro != null ? !a92Euro.equals(that.a92Euro) : that.a92Euro != null) return false;
        if (dieseleuro != null ? !dieseleuro.equals(that.dieseleuro) : that.dieseleuro != null) return false;
        if (pullsdiesel != null ? !pullsdiesel.equals(that.pullsdiesel) : that.pullsdiesel != null) return false;
        if (lpg != null ? !lpg.equals(that.lpg) : that.lpg != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + userId;
        result = 31 * result + (pulls95 != null ? pulls95.hashCode() : 0);
        result = 31 * result + (a95Euro != null ? a95Euro.hashCode() : 0);
        result = 31 * result + (a92Euro != null ? a92Euro.hashCode() : 0);
        result = 31 * result + (dieseleuro != null ? dieseleuro.hashCode() : 0);
        result = 31 * result + (pullsdiesel != null ? pullsdiesel.hashCode() : 0);
        result = 31 * result + (lpg != null ? lpg.hashCode() : 0);
        return result;
    }
}
