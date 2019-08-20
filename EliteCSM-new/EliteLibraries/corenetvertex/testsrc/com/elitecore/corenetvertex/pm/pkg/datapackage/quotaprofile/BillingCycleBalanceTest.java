package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.corenetvertex.constants.CommonConstants;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(HierarchicalContextRunner.class)
public class BillingCycleBalanceTest { //NOSONAR


    public static final long TOTAL = RandomUtils.nextInt();
    public static final long DOWNLOAD = RandomUtils.nextInt();
    public static final long UPLOAD = RandomUtils.nextInt();
    public static final long TIME = RandomUtils.nextInt();
    private BillingCycleBalance balance;

    @Before
    public void setUp() throws Exception {
        balance = new BillingCycleBalance(TOTAL, DOWNLOAD, UPLOAD, TIME);
    }

    public class BalanceCheck {

        @Test
        public void totalShouldGiveAssignedTotal() {
            assertEquals(TOTAL, balance.total());
        }

        @Test
        public void uploadShouldGiveAssignedUpload() {
            assertEquals(UPLOAD, balance.upload());
        }

        @Test
        public void downloadShouldGiveAssignedDownload() {
            assertEquals(DOWNLOAD, balance.download());
        }

        @Test
        public void timeShouldGiveAssignedTime() {
            assertEquals(TIME, balance.time());
        }
    }

    public class BalanceIsExistWhen {

        @Test
        public void AllBalanceIsGreaterThanZero() {
            assertTrue(balance.isExist());
        }

        @Test
        public void AllBalanceIsUndefined() {
            assertTrue(new BillingCycleBalance(CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED).isExist());
        }

        @Test
        public void totalIsUndefinedAndAllOtherBifurcationIsDefined() {
            assertTrue(new BillingCycleBalance(CommonConstants.QUOTA_UNDEFINED, getRandomInt(), getRandomInt(), getRandomInt()).isExist());
        }

        @Test
        public void downloadIsUndefinedAndAllOtherBifurcationIsDefined() {
            assertTrue(new BillingCycleBalance(getRandomInt(), CommonConstants.QUOTA_UNDEFINED, getRandomInt(), getRandomInt()).isExist());
        }

        @Test
        public void uploadIsUndefinedAndAllOtherBifurcationIsDefined() {
            assertTrue(new BillingCycleBalance(getRandomInt(), getRandomInt(), CommonConstants.QUOTA_UNDEFINED, getRandomInt()).isExist());
        }

        @Test
        public void timeIsUndefinedAndAllOtherBifurcationIsDefined() {
            assertTrue(new BillingCycleBalance(getRandomInt(), getRandomInt(), getRandomInt(), CommonConstants.QUOTA_UNDEFINED).isExist());
        }
    }

    public static int getRandomInt() {
        return RandomUtils.nextInt();
    }

    public class BalanceIsNotExistWhen {

        @Test
        public void totalBalanceIsNotPositive() {
            assertFalse(new BillingCycleBalance(0, getRandomInt(), getRandomInt(), getRandomInt()).isExist());
            assertFalse(new BillingCycleBalance(-1, getRandomInt(), getRandomInt(), getRandomInt()).isExist());
        }

        @Test
        public void uploadBalanceIsNotPositive() {
            assertFalse(new BillingCycleBalance(getRandomInt(), getRandomInt(), 0, getRandomInt()).isExist());
            assertFalse(new BillingCycleBalance(getRandomInt(), getRandomInt(), -1, getRandomInt()).isExist());
        }

        @Test
        public void downloadBalanceIsNotPositive() {
            assertFalse(new BillingCycleBalance(getRandomInt(), 0, getRandomInt(), getRandomInt()).isExist());
            assertFalse(new BillingCycleBalance(getRandomInt(), -1, getRandomInt(), getRandomInt()).isExist());
        }

        @Test
        public void timeBalanceIsNotPositive() {
            assertFalse(new BillingCycleBalance(getRandomInt(), getRandomInt(), getRandomInt(), 0).isExist());
            assertFalse(new BillingCycleBalance(getRandomInt(), getRandomInt(), getRandomInt(), -1).isExist());
        }

        @Test
        public void allBalanceIsNegative() {
            assertFalse(new BillingCycleBalance(-1, -1, -1, -1).isExist());
        }
    }

    public class BalanceIsUndefinedCheck {

        @Test
        public void total() {
            assertTrue(new BillingCycleBalance(CommonConstants.QUOTA_UNDEFINED, getRandomInt(), getRandomInt(), getRandomInt()).isTotalUndefined());
        }

        @Test
        public void download() {
            assertTrue(new BillingCycleBalance(getRandomInt(), CommonConstants.QUOTA_UNDEFINED, getRandomInt(), getRandomInt()).isDownloadUndefined());
        }

        @Test
        public void upload() {
            assertTrue(new BillingCycleBalance(getRandomInt(), getRandomInt(), CommonConstants.QUOTA_UNDEFINED, getRandomInt()).isUploadUndefined());
        }

        @Test
        public void time() {
            assertTrue(new BillingCycleBalance(getRandomInt(), getRandomInt(), getRandomInt(), CommonConstants.QUOTA_UNDEFINED).isTimeUndefined());
        }
    }
    
    public class SubtractingBalance {
    	
    	@Test
    	public void reducesExistingBalanceByAmountSubtracted() {
    		balance.subtract(new BalanceImpl(1, 2, 3, 4));
    		
    		assertThat(balance.total(), is(equalTo(TOTAL - 1)));
    		assertThat(balance.download(), is(equalTo(DOWNLOAD - 2)));
    		assertThat(balance.upload(), is(equalTo(UPLOAD - 3)));
    		assertThat(balance.time(), is(equalTo(TIME - 4)));
    	}
    	
    	@Test
    	public void subtractingZeroBalanceHasNoEffect() {
    		balance.subtract(Balance.ZERO);
    		
    		assertThat(balance.total(), is(equalTo(TOTAL)));
    		assertThat(balance.download(), is(equalTo(DOWNLOAD)));
    		assertThat(balance.upload(), is(equalTo(UPLOAD)));
    		assertThat(balance.time(), is(equalTo(TIME)));
    	}
    	
    	@Test
    	public void subtractingGreaterBalanceMakesExistingBalanceNegative() {
    		balance.subtract(new BalanceImpl(TOTAL + 1, DOWNLOAD + 2, UPLOAD + 3, TIME + 4));
    		
    		assertThat(balance.total(), is(equalTo(-1L)));
    		assertThat(balance.download(), is(equalTo(-2L)));
    		assertThat(balance.upload(), is(equalTo(-3L)));
    		assertThat(balance.time(), is(equalTo(-4L)));
    	}
    	
    	public class TotalUndefined {
    		
    		private BillingCycleBalance billingBalance = new BillingCycleBalance(CommonConstants.QUOTA_UNDEFINED, 
    				DOWNLOAD, UPLOAD, TIME);
    		private Balance balanceToSubtract = new BalanceImpl(1, 2, 3, 4);
    		
    		@Test
    		public void totalRemainsUnchanged() {
    			
    			billingBalance.subtract(balanceToSubtract);
    			
    			assertThat(billingBalance.total(), is(equalTo(CommonConstants.QUOTA_UNDEFINED)));
    			assertThat(billingBalance.isTotalUndefined(), is(true));
    		}
    		
    		@Test
    		public void subtractsOtherComponents() {
    			billingBalance.subtract(balanceToSubtract);
    			
    			assertThat(billingBalance.download(), is(equalTo(DOWNLOAD - 2)));
    			assertThat(billingBalance.upload(), is(equalTo(UPLOAD - 3)));
    			assertThat(billingBalance.time(), is(equalTo(TIME - 4)));
    		}
    	}
    	
    	public class DownloadUndefined {
    		
    		private BillingCycleBalance billingBalance = new BillingCycleBalance(TOTAL, 
    				CommonConstants.QUOTA_UNDEFINED, UPLOAD, TIME);
    		private Balance balanceToSubtract = new BalanceImpl(1, 2, 3, 4);
    		
    		@Test
    		public void downloadRemainsUnchanged() {
    			
    			billingBalance.subtract(balanceToSubtract);
    			
    			assertThat(billingBalance.download(), is(equalTo(CommonConstants.QUOTA_UNDEFINED)));
    			assertThat(billingBalance.isDownloadUndefined(), is(true));
    		}
    		
    		@Test
    		public void subtractsOtherComponents() {
    			billingBalance.subtract(balanceToSubtract);
    			
    			assertThat(billingBalance.total(), is(equalTo(TOTAL - 1)));
    			assertThat(billingBalance.upload(), is(equalTo(UPLOAD - 3)));
    			assertThat(billingBalance.time(), is(equalTo(TIME - 4)));
    		}
    	}
    	
    	public class UploadUndefined {
    		
    		private BillingCycleBalance billingBalance = new BillingCycleBalance(TOTAL, 
    				DOWNLOAD, CommonConstants.QUOTA_UNDEFINED, TIME);
    		private Balance balanceToSubtract = new BalanceImpl(1, 2, 3, 4);
    		
    		@Test
    		public void uploadRemainsUnchanged() {
    			
    			billingBalance.subtract(balanceToSubtract);
    			
    			assertThat(billingBalance.upload(), is(equalTo(CommonConstants.QUOTA_UNDEFINED)));
    			assertThat(billingBalance.isUploadUndefined(), is(true));
    		}
    		
    		@Test
    		public void subtractsOtherComponents() {
    			billingBalance.subtract(balanceToSubtract);
    			
    			assertThat(billingBalance.total(), is(equalTo(TOTAL - 1)));
    			assertThat(billingBalance.download(), is(equalTo(DOWNLOAD - 2)));
    			assertThat(billingBalance.time(), is(equalTo(TIME - 4)));
    		}
    	}
    	
    	public class TimeUndefined {
    		
    		private BillingCycleBalance billingBalance = new BillingCycleBalance(TOTAL, 
    				DOWNLOAD, UPLOAD, CommonConstants.QUOTA_UNDEFINED);
    		private Balance balanceToSubtract = new BalanceImpl(1, 2, 3, 4);
    		
    		@Test
    		public void timeRemainsUnchanged() {
    			
    			billingBalance.subtract(balanceToSubtract);
    			
    			assertThat(billingBalance.time(), is(equalTo(CommonConstants.QUOTA_UNDEFINED)));
    			assertThat(billingBalance.isTimeUndefined(), is(true));
    		}
    		
    		@Test
    		public void subtractsOtherComponents() {
    			billingBalance.subtract(balanceToSubtract);
    			
    			assertThat(billingBalance.total(), is(equalTo(TOTAL - 1)));
    			assertThat(billingBalance.download(), is(equalTo(DOWNLOAD - 2)));
    			assertThat(billingBalance.upload(), is(equalTo(UPLOAD - 3)));
    		}
    	}
    }
}