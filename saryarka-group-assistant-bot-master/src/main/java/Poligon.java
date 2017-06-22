import com.turlygazhy.command.CommandType;
import com.turlygazhy.connection_pool.ConnectionPool;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.MemberDao;
import com.turlygazhy.entity.Member;
import com.turlygazhy.google_sheets.SheetsAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by user on 1/14/17.
 */
public class Poligon {
    public static void main(String[] args) throws SQLException {
        DaoFactory factory = DaoFactory.getFactory();
        MemberDao memberDao = factory.getMemberDao();
        Member member = memberDao.selectByUserId(160990487);
        SheetsAdapter sheets = new SheetsAdapter();

        String pkey = "src/main/resources/members-36a5849089da.json";
        String spreadSheetID = "1HyLocKj3xc-auD2zCbk5zpXNioHveMJEYYvpHHVvCEM";

        ArrayList<Member> list = new ArrayList<>();
        list.add(member);
        try {
            sheets.authorize(pkey);
            sheets.writeData(spreadSheetID, "list", 'A', 1, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void clearTable() throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        String tableName = "";//set here table name
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + tableName);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            int id = rs.getInt(1);
            PreparedStatement psDel = connection.prepareStatement("DELETE FROM " + tableName + " WHERE ID=?");
            psDel.setInt(1, id);
            psDel.execute();
        }
    }
}
