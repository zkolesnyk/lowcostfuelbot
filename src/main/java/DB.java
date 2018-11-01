import Entity.FuelsEntity;
import Entity.TaskEntity;
import Entity.UsersEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.Date;
import java.util.List;

public class DB {
    private static final SessionFactory ourSessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    public static void recordsAdd(int userId, String phone, String username, String firstName, String lastName, Date date) {
        try {
            final Session session = getSession();

            String query = String.format("select p from %s p where p.%s = %d", UsersEntity.class.getSimpleName(), "userId", userId);

            UsersEntity user = new UsersEntity();
            user.setUserId(userId);
            user.setPhone(phone);
            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setDateProd(new java.sql.Date(date.getTime()));
            System.out.println(session.createQuery(query).list().isEmpty());

            if (session.createQuery(query).list().isEmpty()) {
                session.beginTransaction();
                session.save(user);
                session.getTransaction().commit();
            }
            System.out.println("Добавлен пользователь");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean checkMobile(int userId) {
        final Session session = getSession();

        String query = String.format("select p from %s p where p.%s = %d", UsersEntity.class.getSimpleName(), "userId", userId);
        return session.createQuery(query).list().isEmpty();
    }

    public static void updateStatus(int userId, String status) {
        try {
            final Session session = getSession();

            String query = String.format("select p from %s p where p.%s = %d", UsersEntity.class.getSimpleName(), "userId", userId);
            UsersEntity user = (UsersEntity) session.createQuery(query).list().get(0);
            System.out.println(user);
            user.setStatus(status);

            session.beginTransaction();
            session.saveOrUpdate(user);
            session.getTransaction().commit();
            System.out.println("Статус пользователя обновлён");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getStatus(int userId, String howMany) {
        final Session session = getSession();

        TaskEntity task = null;

        String queryTask = String.format("select p from %s p where p.%s = %d", TaskEntity.class.getSimpleName(), "userId", userId);
        String queryUsers = String.format("select p from %s p where p.%s = %d", UsersEntity.class.getSimpleName(), "userId", userId);
        if (session.createQuery(queryTask).list().isEmpty()) {
            task = new TaskEntity();
        } else task = (TaskEntity) session.createQuery(queryTask).list().get(0);

        UsersEntity user = (UsersEntity) session.createQuery(queryUsers).list().get(0);
        String status = user.getStatus();
        System.out.println("status " + status + " how many " + howMany);
        task.setUserId(userId);
        switch (status) {
            case "pulls95":
                task.setPulls95(Integer.valueOf(howMany) + (task.getPulls95() == null ? 0 : task.getPulls95()));
                break;
            case "pullsdiesel":
                task.setPullsdiesel(Integer.valueOf(howMany) + (task.getPullsdiesel() == null ? 0 : task.getPullsdiesel()));
                break;
            case "a95euro":
                task.setA95Euro(Integer.valueOf(howMany) + (task.getA95Euro() == null ? 0 : task.getA95Euro()));
                break;
            case "a92euro":
                task.setA92Euro(Integer.valueOf(howMany) + (task.getA92Euro() == null ? 0 : task.getA92Euro()));
                break;
            case "dieseleuro":
                task.setDieseleuro(Integer.valueOf(howMany) + (task.getDieseleuro() == null ? 0 : task.getDieseleuro()));
                break;
            case "lpg":
                task.setLpg(Integer.valueOf(howMany) + (task.getLpg() == null ? 0 : task.getLpg()));
                break;
        }

        session.beginTransaction();
        session.saveOrUpdate(task);
        session.getTransaction().commit();

        return status;
    }

    public static String getOrder(int userId) {
        final Session session = getSession();
        String queryTask = String.format("select p from %s p where p.%s = %d", TaskEntity.class.getSimpleName(), "userId", userId);
        TaskEntity task = (TaskEntity) session.createQuery(queryTask).list().get(0);

//        @SuppressWarnings("unchecked")
//        List<FuelsEntity> list = session.createQuery(queryFuels).list();

        String queryFuels = String.format("select p from %s p", FuelsEntity.class.getSimpleName());
        FuelsEntity pulls95Entity = (FuelsEntity) session.createQuery(queryFuels).list().get(0);
        FuelsEntity a95euroEntity = (FuelsEntity) session.createQuery(queryFuels).list().get(1);
        FuelsEntity a92euroEntity = (FuelsEntity) session.createQuery(queryFuels).list().get(2);
        FuelsEntity dieseleuroEntity = (FuelsEntity) session.createQuery(queryFuels).list().get(3);
        FuelsEntity pullsdieselEntity = (FuelsEntity) session.createQuery(queryFuels).list().get(4);
        FuelsEntity lpgEntity = (FuelsEntity) session.createQuery(queryFuels).list().get(5);

        double pulls95Cost = pulls95Entity.getCost();
        double pullsdieselCost = pullsdieselEntity.getCost();
        double a95euroCost = a95euroEntity.getCost();
        double a92euroCost = a92euroEntity.getCost();
        double dieseleuroCost = dieseleuroEntity.getCost();
        double lpgCost = lpgEntity.getCost();

        int pulls95 = task.getPulls95() == null ? 0 : task.getPulls95();
        int pullsdiesel = task.getPullsdiesel() == null ? 0 : task.getPullsdiesel();
        int a95euro = task.getA95Euro() == null ? 0 : task.getA95Euro();
        int a92euro = task.getA92Euro() == null ? 0 : task.getA92Euro();
        int dieseleuro = task.getDieseleuro() == null ? 0 : task.getDieseleuro();
        int lpg = task.getLpg() == null ? 0 : task.getLpg();

        double costValue = pulls95Cost * pulls95 +
                pullsdieselCost * pullsdiesel +
                a95euroCost * a95euro +
                a92euroCost * a92euro +
                dieseleuroCost * dieseleuro +
                lpgCost * lpg;

        String order = String.format("Ваш заказ:%n*%s*: %d л (%.2f грн/л)%n*%s*: %d л (%.2f грн/л)%n*%s*: %d л (%.2f грн/л)%n*%s*: %d л (%.2f грн/л)%n*%s*: %d л (%.2f грн/л)%n*%s*: %d л (%.2f грн/л)",
                LowCostFuelBot.fuelName("pulls95"), pulls95, pulls95Cost,
                LowCostFuelBot.fuelName("pullsdiesel"), pullsdiesel, pullsdieselCost,
                LowCostFuelBot.fuelName("a95euro"), a95euro, a95euroCost,
                LowCostFuelBot.fuelName("a92euro"), a92euro, a92euroCost,
                LowCostFuelBot.fuelName("dieseleuro"), dieseleuro, dieseleuroCost,
                LowCostFuelBot.fuelName("lpg"), lpg, lpgCost);

        String cost = String.format("%n%nОбщей стоимостью на *%.2f* грн", costValue);

        return order + cost;
    }

    public static void setPulls95Cost(double cost) {
        final Session session = getSession();

        String queryFuels = String.format("select p from %s p", FuelsEntity.class.getSimpleName());
        FuelsEntity pulls95Entity = (FuelsEntity) session.createQuery(queryFuels).list().get(0);

        pulls95Entity.setCost(cost);

        session.beginTransaction();
        session.saveOrUpdate(pulls95Entity);
        session.getTransaction().commit();
    }

    public static void setA95euroCost(double cost) {
        final Session session = getSession();

        String queryFuels = String.format("select p from %s p", FuelsEntity.class.getSimpleName());
        FuelsEntity a95euroEntity = (FuelsEntity) session.createQuery(queryFuels).list().get(1);

        a95euroEntity.setCost(cost);

        session.beginTransaction();
        session.saveOrUpdate(a95euroEntity);
        session.getTransaction().commit();
    }

    public static void setA92euroCost(double cost) {
        final Session session = getSession();

        String queryFuels = String.format("select p from %s p", FuelsEntity.class.getSimpleName());
        FuelsEntity a92euroEntity = (FuelsEntity) session.createQuery(queryFuels).list().get(2);

        a92euroEntity.setCost(cost);

        session.beginTransaction();
        session.saveOrUpdate(a92euroEntity);
        session.getTransaction().commit();
    }

    public static void setDieseleuroCost(double cost) {
        final Session session = getSession();

        String queryFuels = String.format("select p from %s p", FuelsEntity.class.getSimpleName());
        FuelsEntity dieseleuroEntity = (FuelsEntity) session.createQuery(queryFuels).list().get(3);

        dieseleuroEntity.setCost(cost);

        session.beginTransaction();
        session.saveOrUpdate(dieseleuroEntity);
        session.getTransaction().commit();
    }

    public static void setPullsdieselCost(double cost) {
        final Session session = getSession();

        String queryFuels = String.format("select p from %s p", FuelsEntity.class.getSimpleName());
        FuelsEntity pullsdieselEntity = (FuelsEntity) session.createQuery(queryFuels).list().get(4);

        pullsdieselEntity.setCost(cost);

        session.beginTransaction();
        session.saveOrUpdate(pullsdieselEntity);
        session.getTransaction().commit();
    }

    public static void setLpgCost(double cost) {
        final Session session = getSession();

        String queryFuels = String.format("select p from %s p", FuelsEntity.class.getSimpleName());
        FuelsEntity lpgEntity = (FuelsEntity) session.createQuery(queryFuels).list().get(5);

        lpgEntity.setCost(cost);

        session.beginTransaction();
        session.saveOrUpdate(lpgEntity);
        session.getTransaction().commit();
    }

    public static void cancelOrder(int userId) {
        final Session session = getSession();
        String query = String.format("select p from %s p where p.%s = %d", TaskEntity.class.getSimpleName(), "userId", userId);
        TaskEntity task = (TaskEntity) session.createQuery(query).list().get(0);

        task.setPulls95(0);
        task.setA95Euro(0);
        task.setA92Euro(0);
        task.setDieseleuro(0);
        task.setPullsdiesel(0);
        task.setLpg(0);

        session.beginTransaction();
        session.saveOrUpdate(task);
        session.getTransaction().commit();
    }

    public static List<FuelsEntity> getFuelsList()
    {
        final Session session = getSession();
        System.out.println("\nЧтение записей таблицы");
        String query = String.format("select p from %s p", FuelsEntity.class.getSimpleName());
        System.out.println(query);

        @SuppressWarnings("unchecked")
        List<FuelsEntity> list = session.createQuery(query).list();

        return list;
    }

    public static List<UsersEntity> getUserFromId(int userId)
    {
        final Session session = getSession();

        System.out.println("\nЧтение записей таблицы");
        String query = String.format("select p from %s p where p.userId = %d", UsersEntity.class.getSimpleName(), userId);
        System.out.println(query);

        @SuppressWarnings("unchecked")
        List<UsersEntity> list = session.createQuery(query).list();

        return list;
    }

    public static List<UsersEntity> getUsersList()
    {
        final Session session = getSession();
        String query = String.format("select p from %s p", UsersEntity.class.getSimpleName());
        System.out.println(query);

        @SuppressWarnings("unchecked")
        List<UsersEntity> list = session.createQuery(query).list();

        return list;
    }
}