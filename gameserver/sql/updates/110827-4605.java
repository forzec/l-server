import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.mmocore.commons.dbutils.DbUtils;
import org.mmocore.gameserver.Config;
import org.mmocore.gameserver.database.DatabaseFactory;

/**
 * @author VISTALL
 * @date 18:13/27.08.2011
 */
public class QuestConverter
{
	public static void main(String... arg) throws Exception
	{
		Config.load();
		Class.forName(Config.DATABASE_DRIVER).newInstance();
		DatabaseFactory.getInstance().getConnection().close();

		Map<String, Integer> map = new HashMap<String, Integer>();

		Connection con = DatabaseFactory.getInstance().getConnection();

		//
		Statement statement = con.createStatement();
		statement.execute("ALTER TABLE character_quests ADD COLUMN quest_id INT NOT NULL");
		DbUtils.close(statement);
		//

		//
		statement = con.createStatement();
		ResultSet rset = statement.executeQuery("SELECT name FROM character_quests");
		while(rset.next())
		{
			String questName = rset.getString("name");

			Integer questId = map.get(questName);
			if(questId == null)
				map.put(questName, Integer.parseInt(questName.split("_")[1]));
		}
		DbUtils.close(statement, rset);
		//

		//
		for(Map.Entry<String, Integer> entry : map.entrySet())
		{
			PreparedStatement temp = con.prepareStatement("UPDATE character_quests SET quest_id=? WHERE name=?");
			temp.setInt(1, entry.getValue());
			temp.setString(2, entry.getKey());
			temp.execute();

			DbUtils.close(temp);
		}
		//

		//
		statement = con.createStatement();
		statement.execute("ALTER TABLE character_quests DROP PRIMARY KEY, ADD PRIMARY KEY(char_id, quest_id, var), DROP COLUMN name");
		DbUtils.close(statement);
		//

		DbUtils.close(con);
	}
}
