```
package clickhouse;

import java.sql.*;

public class ClickHouseDemo {

    public static void main(String[] args) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        String url = "jdbc:clickhouse://0.0.0.0:8123/table_merge";
        try{
            Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from uact");
            ResultSetMetaData metaData = resultSet.getMetaData();
            int i=1;
            while (resultSet.next()&&i<=metaData.getColumnCount()){
                String columnName = metaData.getColumnName(i);
                String value = resultSet.getString(columnName);
                System.out.printf("%s   %s%n",columnName,value);
                i++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(statement!=null){
                statement.close();
            }
            if(connection!=null){
                connection.close();
            }
        }

    }
}
```
