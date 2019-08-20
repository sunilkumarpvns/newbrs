package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(HierarchicalContextRunner.class)
public class BalanceImplTest {

    private BalanceImpl balance;

    @Before
    public void setUp() throws Exception {
        balance = new BalanceImpl(1,2,3,4);
    }

    @Test
    public void allValuesAssignedProperly() throws Exception {
        assertThat(balance.total(), is(1l));
        assertThat(balance.upload(), is(3l));
        assertThat(balance.download(), is(2l));
        assertThat(balance.time(), is(4l));
    }

    public class BalanceCheck {
        public class BalanceIsExistWhen {

            @Test
            public void AllBalanceIsGreaterThanZero() {
                assertTrue(balance.isExist());
            }
        }

        public class BalanceIsNotExistWhen {

            //FIXME USE DATA DRIVEN WHEN USE JUNIT 5.
            //iN JUNIT4 DATA DRIVEN IS NOT SUPPORTED IN HIERARCHICAL RUNNER
            @Test
            public void totalBalanceIsNotPositive() {
                assertFalse(new BalanceImpl(0,1,1,1).isExist());
                assertFalse(new BalanceImpl(-1,1,1,1).isExist());
            }

            @Test
            public void uploadBalanceIsNotPositive() {
                assertFalse(new BalanceImpl(1,1,0,1).isExist());
                assertFalse(new BalanceImpl(1,1,-1,1).isExist());
            }

            @Test
            public void downloadBalanceIsNotPositive() {
                assertFalse(new BalanceImpl(1,0,1,1).isExist());
                assertFalse(new BalanceImpl(1,-1,1,1).isExist());
            }

            @Test
            public void timeBalanceIsNotPositive() {
                assertFalse(new BalanceImpl(1,1,1,0).isExist());
                assertFalse(new BalanceImpl(1,1,1,-1).isExist());
            }


        }
    }

    

    public class EqualityTest {

        public class TwoBalancesAreEqualsWhen {

            @Test
            public void allBalancesAreSame() {
                assertTrue(balance.equals(new BalanceImpl(1,2,3,4)));
            }

            @Test
            public void sameReference() {
                assertTrue(balance.equals(balance));
            }
        }

        public class TwoBalancesAreNotEqualsWhen{


            @Test
            public void totalIsNotSame() {
                assertFalse(balance.equals(new BalanceImpl(2,2,3,4)));
            }

            @Test
            public void downloadIsNotSame() {
                assertFalse(balance.equals(new BalanceImpl(1,3,3,4)));
            }

            @Test
            public void uploadIsNotSame() {
                assertFalse(balance.equals(new BalanceImpl(1,2,4,4)));
            }

            @Test
            public void timeIsNotSame() {
                assertFalse(balance.equals(new BalanceImpl(1,2,3,5)));
            }

        }

        @Test
        public void notEqualsWhenReferenceIsNot() {
            assertFalse(balance.equals(null));
        }
        @Test
        public void notEqualWhenBothAreFromDifferentClass() {
            assertFalse(balance.equals(new Balance(){  

                @Override
                public long total() {
                    return balance.total();
                }

                @Override
                public long upload() {
                    return balance.upload();
                }

                @Override
                public long download() {
                    return balance.download();
                }

                @Override
                public long time() {
                    return balance.time();
                }

                @Override
                public boolean isExist() {
                    return balance.isExist();
                }

				@Override
				public void subtract(Balance balance) {
					// No-op
					
				}
            }));
        }
    }

    public class SubtractingBalances {
    	
    	private BalanceImpl balance = new BalanceImpl(10, 11, 12, 13);
    	
    	@Test
    	public void reducesExistingBalanceByAmountSubtracted() {
    		balance.subtract(new BalanceImpl(1, 3, 5, 7));
    		
    		assertThat(balance.total(), is(equalTo(9L)));
    		assertThat(balance.download(), is(equalTo(8L)));
    		assertThat(balance.upload(), is(equalTo(7L)));
    		assertThat(balance.time(), is(equalTo(6L)));
    	}
    	
    	@Test
    	public void subtractingZeroBalanceHasNoEffect() {
    		balance.subtract(Balance.ZERO);
    		
    		assertThat(balance.total(), is(equalTo(10L)));
    		assertThat(balance.download(), is(equalTo(11L)));
    		assertThat(balance.upload(), is(equalTo(12L)));
    		assertThat(balance.time(), is(equalTo(13L)));
    	}
    	
    	@Test
    	public void subtractingGreaterBalanceMakesExistingBalanceNegative() {
    		balance.subtract(new BalanceImpl(100, 103, 105, 107));
    		
    		assertThat(balance.total(), is(equalTo(-90L)));
    		assertThat(balance.download(), is(equalTo(-92L)));
    		assertThat(balance.upload(), is(equalTo(-93L)));
    		assertThat(balance.time(), is(equalTo(-94L)));
    	}
    }
}