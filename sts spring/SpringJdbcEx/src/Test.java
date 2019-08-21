import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class Test {

	public static void main(String[] args) {
		ApplicationContext ac=new ClassPathXmlApplicationContext("jdbcconfig.xml");
		JdbcTemplate jt=(JdbcTemplate)ac.getBean("jtObj");
		String sql="insert into emptab values(?,?,?)";
		int count=jt.update(sql,11,"a",22);
		System.out.println(count);
		
	
		String sql1="update emptab set ename=? where eid=?";
		
		int count1=jt.update(sql1,"BB",10);
		System.out.println(count);
		
	}
	
}
