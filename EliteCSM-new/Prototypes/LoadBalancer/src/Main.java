import loadbalancer.FailOverLoadBalancer;
import loadbalancer.LeastResponseTimeLoadBalance;
import loadbalancer.PredicateFactory;
import loadbalancer.QueueLoadBalancer;
import loadbalancer.RandomLoadBalance;
import loadbalancer.StickyLoadBalance;
import core.Function;
import core.Key;
import core.Predicate;
import core.Processor;

/**
 * Requested to read README.md for understanding the project
 * 
 * @author narendra.pathai
 *
 */
public class Main {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("*********** Round Robin **************");
		QueueLoadBalancer<String, StringProcessor> rrLoadBalancer = 
			new QueueLoadBalancer<String, StringProcessor>();
		
		StringProcessor processor1 = new StringProcessor();
		StringProcessor processor2 = new StringProcessor();
		
		rrLoadBalancer.addProcessor(processor1);
		rrLoadBalancer.addProcessor(processor2);
		
		rrLoadBalancer.process("First");
		
		assert processor1.getString().equals("First");
		assert processor2.getString() == null;
		
		rrLoadBalancer.process("Second");
		assert processor2.getString().equals("Second");
		
		rrLoadBalancer.process("Third");
		assert processor1.getString().equals("Third");
		
		System.out.println("*********** Failover **************");
		
		FailOverLoadBalancer<Request, Processor<Request>> failoverLB = 
			new FailOverLoadBalancer<Request, Processor<Request>>(new PredicateFactory<Request>() {

				public Predicate<Request> predicate() {
					return new Predicate<Request>() {

						public boolean apply(Request input) {
							return input.getAccountData() == null;
						}
					};
				}
			});
		
		failoverLB.addProcessor(new AccountDataProcessor("Processor 1", null));
		failoverLB.addProcessor(new AccountDataProcessor("Processor 2", null));
		failoverLB.addProcessor(new AccountDataProcessor("Processor 3", null));
		
		Request request = new Request(new KeyImpl(""));
		failoverLB.process(request);
		assert request.getAccountData() == null;
		
		
		System.out.println("*********** Sticky **************");
		
		QueueLoadBalancer<Request, AccountDataProcessor> stickyLB = 
			new QueueLoadBalancer<Request, AccountDataProcessor>(
					new StickyLoadBalance<Request, AccountDataProcessor>(new Function<Request, Key>() {

						public Key apply(final Request input) {
							return input.getKey();
						}
					}));
		
		stickyLB.addProcessor(new AccountDataProcessor("Processor 1", null));
		stickyLB.addProcessor(new AccountDataProcessor("Processor 2", null));
		stickyLB.addProcessor(new AccountDataProcessor("Processor 3", null));
		stickyLB.addProcessor(new AccountDataProcessor("Processor 4", null));
		stickyLB.addProcessor(new AccountDataProcessor("Processor 5", null));
		stickyLB.addProcessor(new AccountDataProcessor("Processor 6", null));
		stickyLB.addProcessor(new AccountDataProcessor("Processor 7", null));
		stickyLB.addProcessor(new AccountDataProcessor("Processor 8", null));
		
		stickyLB.process(new Request(new KeyImpl("Key1")));
		stickyLB.process(new Request(new KeyImpl("Key2")));
		stickyLB.process(new Request(new KeyImpl("Key1")));
		stickyLB.process(new Request(new KeyImpl("Key2")));
		stickyLB.process(new Request(new KeyImpl("Key2")));
		stickyLB.process(new Request(new KeyImpl("Key2")));
		
		System.out.println("*********** Random **************");
		
		QueueLoadBalancer<Request, AccountDataProcessor> randomLB = 
			new QueueLoadBalancer<Request, AccountDataProcessor>(
					new RandomLoadBalance<Request, AccountDataProcessor>());
		
		randomLB.addProcessor(new AccountDataProcessor("Processor 1", null));
		randomLB.addProcessor(new AccountDataProcessor("Processor 2", null));
		randomLB.addProcessor(new AccountDataProcessor("Processor 3", null));
		randomLB.addProcessor(new AccountDataProcessor("Processor 4", null));
		randomLB.addProcessor(new AccountDataProcessor("Processor 5", null));
		randomLB.addProcessor(new AccountDataProcessor("Processor 6", null));
		randomLB.addProcessor(new AccountDataProcessor("Processor 7", null));
		randomLB.addProcessor(new AccountDataProcessor("Processor 8", null));
		
		randomLB.process(new Request(new KeyImpl("Key1")));
		randomLB.process(new Request(new KeyImpl("Key2")));
		randomLB.process(new Request(new KeyImpl("Key1")));
		randomLB.process(new Request(new KeyImpl("Key2")));
		randomLB.process(new Request(new KeyImpl("Key2")));
		randomLB.process(new Request(new KeyImpl("Key2")));
		
		
		System.out.println("*********** Least Response time **************");
		
		QueueLoadBalancer<Request, AccountDataProcessor> leastResponseTimeLB = 
			new QueueLoadBalancer<Request, AccountDataProcessor>(
					new LeastResponseTimeLoadBalance<Request, AccountDataProcessor>());
		
		leastResponseTimeLB.addProcessor(new AccountDataProcessor("Processor 1", null));
		leastResponseTimeLB.addProcessor(new AccountDataProcessor("Processor 2", null));
		leastResponseTimeLB.addProcessor(new AccountDataProcessor("Processor 3", null));
		leastResponseTimeLB.addProcessor(new AccountDataProcessor("Processor 4", null));
		leastResponseTimeLB.addProcessor(new AccountDataProcessor("Processor 5", null));
		leastResponseTimeLB.addProcessor(new AccountDataProcessor("Processor 6", null));
		leastResponseTimeLB.addProcessor(new AccountDataProcessor("Processor 7", null));
		leastResponseTimeLB.addProcessor(new AccountDataProcessor("Processor 8", null));

		for (int i = 0; i < 10; i++) {
			leastResponseTimeLB.process(new Request(new KeyImpl("Key1")));
			Thread.sleep(5000);
		}
		
		
		System.out.println("*********** Round Robin + Least response time **************");
		
		QueueLoadBalancer<Request, AccountDataProcessor> rrAndLrtLB = 
			new QueueLoadBalancer<Request, AccountDataProcessor>(
					new RandomLoadBalance<Request, AccountDataProcessor>());
		
		rrAndLrtLB.addProcessor(new AccountDataProcessor("Processor 1", null));
		rrAndLrtLB.addProcessor(new AccountDataProcessor("Processor 2", null));
		rrAndLrtLB.addProcessor(new AccountDataProcessor("Processor 3", null));
		rrAndLrtLB.addProcessor(new AccountDataProcessor("Processor 4", null));
		rrAndLrtLB.addProcessor(new AccountDataProcessor("Processor 5", null));
		rrAndLrtLB.addProcessor(new AccountDataProcessor("Processor 6", null));
		rrAndLrtLB.addProcessor(new AccountDataProcessor("Processor 7", null));
		rrAndLrtLB.addProcessor(new AccountDataProcessor("Processor 8", null));

		for (int i = 0; i < 10; i++) {
			leastResponseTimeLB.process(new Request(new KeyImpl("Key1")));
			Thread.sleep(5000);
		}
	}
	
	static class KeyImpl implements Key {
		private final String value;

		public String getValue() {
			return value;
		}

		public KeyImpl(String value) {
			this.value = value;
		}
		
		@Override
		public int hashCode() {
			return value.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			return value.equals(((KeyImpl)obj).value);
		}
	}
	
	static class Request {
		private final Key key;
		private Object accountData;

		public Request(Key key) {
			this.key = key;
		}
		
		public Key getKey() {
			return key;
		}
		
		public Object getAccountData() {
			return accountData;
		}

		public void setAccountData(Object accountData) {
			this.accountData = accountData;
		}
	}
	
	static class AccountDataProcessor implements Processor<Request> {
		private Object accountData;
		private final String name;
		private final double averageResponseTime;
		
		public AccountDataProcessor(String name, Object accountData) {
			this.name = name;
			this.accountData = accountData;
			averageResponseTime = 0;
		}
		
		public AccountDataProcessor(String name, double averageResponseTime) {
			this.name = name;
			this.averageResponseTime = averageResponseTime;
		}
		
		public void process(Request input) {
			System.out.println(name);
			input.setAccountData(accountData);
		}

		@Override
		public double averageResponseTime() {
			return Math.random();
		}
	}

	static class StringProcessor implements Processor<String> {

		private String string;

		public String getString() {
			return string;
		}

		public void process(String input) {
			this.string = input;
		}

		@Override
		public double averageResponseTime() {
			return Math.random();
		}
	}
}