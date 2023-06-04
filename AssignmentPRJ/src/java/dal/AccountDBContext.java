package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Account;
import model.Feature;
import model.Group;

public class AccountDBContext extends DBContext {

    public ArrayList<Account> getAllAccount() {
        ArrayList<Account> account = new ArrayList<>();
        try {
            String sql = "select a.username, a.[password], a.displayname, g.gid, g.gname from Account a \n"
                    + "left join GroupAccount ga on a.username = ga.username\n"
                    + "left join [Group] g on g.gid = ga.gid";
            PreparedStatement stm = connection.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Account a = new Account();
                a.setUsername(rs.getString("username"));
                a.setPassword(rs.getString("password"));
                a.setDisplayName(rs.getString("displayname"));

                Group gr = new Group();
                gr.setGid(rs.getInt("gid"));
                gr.setGname(rs.getString("gname"));
                a.setGro(gr);
                account.add(a);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccountDBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return account;
    }

    public Account getAccount(String username, String password) {

        try {
            String sql = "select a.username, a.password, a.displayname, f.fid, f.url, g.gid, g.gname from Account a \n"
                    + "left join GroupAccount ga on a.username = ga.username\n"
                    + "left join [Group] g on g.gid = ga.gid\n"
                    + "left join GroupFeature gf on gf.gid = g.gid\n"
                    + "left join Feature f on f.fid = gf.fid\n"
                    + "where a.username = ? and a.password = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, username);
            stm.setString(2, password);
            ResultSet rs = stm.executeQuery();
            Account a = null;
            while (rs.next()) {
                if (a == null) {
                    a = new Account();
                    a.setUsername(username);
                    a.setPassword(password);

                    Group gr = new Group();
                    gr.setGid(rs.getInt("gid"));
                    gr.setGname(rs.getString("gname"));
                    a.setGro(gr);
                }
                int fid = rs.getInt("fid");
                if (fid != 0) {
                    Feature f = new Feature();
                    f.setId(fid);
                    f.setUrl(rs.getString("url"));
                    a.getFeatures().add(f);
                }
            }
            return a;

        } catch (SQLException ex) {
            Logger.getLogger(AccountDBContext.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Account checkAccountExist(String user) {
        String sql = "select * from account as a\n"
                + "where a.username = ?\n";
        try {
            PreparedStatement stm = connection.prepareStatement(sql);

            stm.setString(1, user);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Account a = new Account();
                a.setUsername(rs.getString(1));
                a.setPassword(rs.getString(2));
                a.setDisplayName(rs.getString(3));

            }
        } catch (SQLException ex) {
            Logger.getLogger(AccountDBContext.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void singup(String user, String pass, String displayName) {
        String sql = "insert into account\n"
                + "values(?,?,?)";
        try {
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, user);
            stm.setString(2, pass);
            stm.setString(3, displayName);
            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(AccountDBContext.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void deleteAccount(String username) {
        try {
            connection.setAutoCommit(false);
            String sql_remove_fl = "Delete [Following] where username = ?";
            PreparedStatement stm_remove_fl = connection.prepareStatement(sql_remove_fl);
            stm_remove_fl.setString(1, username);
            stm_remove_fl.executeUpdate();

            String sql_remove_cert = "Delete GroupAccount where username = ?";
            PreparedStatement stm_remove_cert = connection.prepareStatement(sql_remove_cert);
            stm_remove_cert.setString(1, username);
            stm_remove_cert.executeUpdate();

            String sql = "Delete Account where username = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, username);
            stm.executeUpdate();

            connection.commit();
        } catch (SQLException ex) {
            Logger.getLogger(AccountDBContext.class.getName()).log(Level.SEVERE, null, ex);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(AccountDBContext.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(AccountDBContext.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void updateAccount(Account acc) {

        try {
            connection.setAutoCommit(false);
            String sql = "UPDATE [Account]\n"
                    + "SET [username] = ?,\n"
                    + "[password] = ?,\n"
                    + "[displayname] = ?\n"
                    + "WHERE username = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, acc.getUsername());
            stm.setString(2, acc.getPassword());
            stm.setString(3, acc.getDisplayName());
            stm.setString(4, acc.getUsername());
            stm.executeUpdate();

            String sql_remove_cert = "Delete GroupAccount where username = ?";
            PreparedStatement stm_remove_cert = connection.prepareStatement(sql_remove_cert);
            stm_remove_cert.setString(1, acc.getUsername());
            stm_remove_cert.executeUpdate();

            String sql_insert_cert = "INSERT INTO [GroupAccount]\n"
                    + "           ([gid]\n"
                    + "           ,[username])\n"
                    + "     VALUES\n"
                    + "           (?\n"
                    + "           ,?)";
            PreparedStatement stm_insert_cert = connection.prepareStatement(sql_insert_cert);
            stm_insert_cert.setInt(1, acc.getGro().getGid());
            stm_insert_cert.setString(2, acc.getUsername());
            stm_insert_cert.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            Logger.getLogger(AccountDBContext.class.getName()).log(Level.SEVERE, null, ex);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(AccountDBContext.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(AccountDBContext.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Account getAccountByUsername(String username) {
        String sql = "select a.username, a.[password], a.displayname, g.gid, g.gname from Account a \n"
                + "left join GroupAccount ga on a.username = ga.username\n"
                + "left join [Group] g on g.gid = ga.gid\n"
                + "where a.username = ?";
        try {
            PreparedStatement stm = connection.prepareStatement(sql);

            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            Account a = null;
            while (rs.next()) {
                if (a == null) {
                    a = new Account();
                    a.setUsername(username);
                    a.setPassword(rs.getString("password"));
                    a.setDisplayName(rs.getString("displayname"));

                    Group gr = new Group();
                    gr.setGid(rs.getInt("gid"));
                    gr.setGname(rs.getString("gname"));
                    a.setGro(gr);
                }
            }
            return a;
        } catch (SQLException ex) {
            Logger.getLogger(AccountDBContext.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public static void main(String[] args) {


        for (int i = 0; i < 100; i++) {
            System.out.println("[Login - " + i + "]");
        }

    }
}
