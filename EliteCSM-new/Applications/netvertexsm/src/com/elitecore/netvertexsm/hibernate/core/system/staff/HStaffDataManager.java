package com.elitecore.netvertexsm.hibernate.core.system.staff;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.sm.acl.StaffProfilePictureData;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.StaffDataManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffGroupRoleRelData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.util.EliteAssert;
import com.elitecore.netvertexsm.util.PasswordUtility;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class HStaffDataManager extends HBaseDataManager implements StaffDataManager{
		/**
		 * @author  dhavalraval
		 * @param 	radiusPolicyData
		 * @return  List
		 * @throws  DataManagerException
		 * @purpose This method returns the list of StaffData.
		 */
		private static final String MODULE = "STAFF DATA MANAGER";
		public List getList() throws DataManagerException{
			List staffList = null;
			 try{
		            Session session = getSession();
		            Criteria criteria = session.createCriteria(StaffData.class);
		            
		            staffList = criteria.list();

		      }catch(HibernateException hExp){
		          throw new DataManagerException(hExp.getMessage(), hExp);
		      }catch(Exception exp){
		    	  throw new DataManagerException(exp.getMessage(), exp);
		      }
			return staffList;
		}
		
		/*public List getStaffGroupRelList(String staffId) throws DataManagerException{
			List lstStaffGroupRelList = null;
			try {
				Session session = getSession();
				Criteria criteria = session.createCriteria(StaffGroupRelData.class);
				criteria.add(Restrictions.eq("staffId",staffId));
				criteria.createAlias("groupData","groupData");
//				criteria.add(Restrictions.eq("groupData.",))
				lstStaffGroupRelList = criteria.list();
			} catch (HibernateException hExp) {
				throw new DataManagerException(hExp.getMessage(),hExp);
			} catch (Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
			}
			return lstStaffGroupRelList;
		}*/
		
		/**
		 * @author  dhavalraval
		 * @param   staffId
		 * @return  List
		 * @throws  DataManagerException
		 * @purpose This method returs the list of GroupData for specific StaffId.
		 */
		/*public List getStaffRoleRelList(long staffId) throws DataManagerException{
			List lstStaffRoleRelList = null;
			
			try {
				Session session = getSession();
				
				Criteria criteria = session.createCriteria(RoleData.class);
				criteria.createAlias("staffRoleRel","staffRoleRelData");
				criteria.add(Restrictions.eq("staffRoleRelData.staffId",staffId));
				
				lstStaffRoleRelList = criteria.list();
			} catch (HibernateException hExp) {
				throw new DataManagerException(hExp.getMessage(),hExp);
			} catch (Exception exp) {
				throw new DataManagerException(exp.getMessage(),exp);
			}
			return lstStaffRoleRelList;
		}
		*/
		
		
		/**
		 * @author  ishani.bhatt
		 * @param   staffId
		 * @return  List
		 * @throws  DataManagerException
		 * @purpose This method returns the list of Role and Group data based on staffId
		 */
		public List<StaffGroupRoleRelData> getStaffGroupRoleRelList(long staffId) throws DataManagerException{
			List<StaffGroupRoleRelData> staffGroupRoleRelList = Collections.emptyList();
			try {
				Session session = getSession();
				
				Criteria criteria = session.createCriteria(StaffData.class);
				criteria.add(Restrictions.eq("staffId",staffId));
				List staffDatas = criteria.list();
				if(Collectionz.isNullOrEmpty(staffDatas) == false){
					StaffData staffData = (StaffData) staffDatas.get(0);
					staffGroupRoleRelList = staffData.getStaffGroupRoleRelationList();
				}
			} catch (HibernateException hExp) {
				throw new DataManagerException(hExp.getMessage(),hExp);
			} catch (Exception exp) {
				throw new DataManagerException(exp.getMessage(),exp);
			}
			return staffGroupRoleRelList;
		}
		
		/**
		 * @author  dhavalraval
		 * @param   staffData
		 * @return  IStaffData object
		 * @throws  DataManagerException
		 * @purpose This method is generated to get list of StaffData.
		 */
		public List getList(IStaffData staffData) throws DataManagerException{
			List staffList = null;
			try{
		        Session session = getSession();
		        Criteria criteria = session.createCriteria(StaffData.class);
		            
		        if(staffData.getStaffId() != 0)         
		          criteria.add(Restrictions.eq("staffId",staffData.getStaffId()));

		        if(staffData.getName() != null)
		          criteria.add(Restrictions.eq("name",staffData.getName()));

		        if(staffData.getUserName() != null)
		          criteria.add(Restrictions.eq("userName",staffData.getUserName()));  

		        staffList = criteria.list();

		    }catch(HibernateException hExp){
		            throw new DataManagerException(hExp.getMessage(), hExp);
		    }catch(Exception exp){
		        	throw new DataManagerException(exp.getMessage(), exp);
		    }
			return staffList;
		}
		
		/**
		 * @author  dhavalraval
		 * @param   staffData
		 * @param   pageNo
		 * @param   pageSize
		 * @return  List
		 * @throws  DataManagerException
		 * @purpose This method is return list with specific arguments.
		 */
		public PageList search(IStaffData staffData, int pageNo, int pageSize,List lstHiddenUser) throws DataManagerException{
			PageList pageList = null;
			
			try{
				Session session = getSession();
				Criteria criteria = session.createCriteria(StaffData.class);
	            
	            if(staffData.getUserName() != null && staffData.getUserName().length()>0){
	            	criteria.add(Restrictions.ilike("userName",staffData.getUserName()));
	            }
	            
		   if((staffData.getName() != null && staffData.getName().length()>0 )){
	            	criteria.add(Restrictions.ilike("name",staffData.getName()));
	            }
				
		   if(staffData.getCommonStatusId() !=null ){
	            	criteria.add(Restrictions.ilike("commonStatusId",staffData.getCommonStatusId()));
	            }
                   
                   if(lstHiddenUser != null)
                   {
                      Iterator itLstHiddenUser = lstHiddenUser.iterator();
                       while(itLstHiddenUser.hasNext())
                           criteria.add(Restrictions.ne("userName",(String) itLstHiddenUser.next()));
                   }
                
	        int totalItems = criteria.list().size();

            	criteria.setFirstResult(((pageNo-1) * pageSize));

            	if (pageSize > 0 ){
            		criteria.setMaxResults(pageSize);
            	}
            	
            	List staffList = criteria.list();
	           
	            long totalPages = (long)Math.ceil(totalItems/10);
	            pageList = new PageList(staffList, pageNo, totalPages ,totalItems);
			}catch(HibernateException hExp){
				throw new DataManagerException(hExp.getMessage(), hExp);
			}catch(Exception exp){
				throw new DataManagerException(exp.getMessage(), exp);
			}
			return pageList;
		}
		
		
		/**
		 * @author  dhavalraval
		 * @param   staffData
		 * @throws  DataManagerException
		 * @purpose This method is generated to UpdateStaffAccessGroup details.
		 */
		public void updateStaffGroupRoleRelation(IStaffData staffData) throws DataManagerException {
			try {
				Session session = getSession();
				for(Iterator staffGroupRelIterator = staffData.getStaffGroupRoleRelationList().iterator();staffGroupRelIterator.hasNext();){
					StaffGroupRoleRelData relationData = (StaffGroupRoleRelData) staffGroupRelIterator.next();
					session.save(relationData);
				}
			} catch (HibernateException hExp) {
				hExp.printStackTrace();
				throw new DataManagerException(hExp.getMessage(),hExp);
			} catch (Exception exp) {
				exp.printStackTrace();
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}
		
		/**
		 * @author  dhavalraval
		 * @param   staffData
		 * @throws  DataManagerException
		 * @purpose This method is generated to create StaffData.
		 */
		public void create(IStaffData staffData) throws DataManagerException{
			try{
				Session session = getSession();
				session.save(staffData);
			 	
				 	
		    }catch(HibernateException hExp){
		       	hExp.printStackTrace();
		        throw new DataManagerException(hExp.getMessage(), hExp);
		    }catch(Exception exp){
		      	exp.printStackTrace();
		      	throw new DataManagerException(exp.getMessage(),exp);
		    }
		}
		
		/**
		 * @author  dhavalraval
		 * @param   staffId
		 * @param   commonStatus
		 * @throws  DataManagerException
		 * @purpose This method is generated to update the Status of StaffData.
		 */
		public void updateStatus(long staffId, String commonStatus, Timestamp statusChangeDate) throws DataManagerException{	
			Session session = getSession();			
			StaffData staffData = null;

			try{
				Criteria criteria = session.createCriteria(StaffData.class);
				staffData = (StaffData)criteria.add(Restrictions.eq("staffId",staffId))
											   .uniqueResult();

				staffData.setCommonStatusId(commonStatus);
				staffData.setStatusChangeDate(statusChangeDate);
				session.update(staffData);
				session.flush();
			}catch(HibernateException hExp){
				throw new DataManagerException(hExp.getMessage(), hExp);
			}catch(Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}
		
		/**
		 * @author  dhavalraval
		 * @param   staffId
		 * @throws  DataManagerException
		 * @purpose This method is generated to delete StaffData.
		 */
		public void delete(long staffId)  throws DataManagerException{
			StaffData staffData = null;
			 
			try{
				 Session session = getSession();
				 Criteria criteria = session.createCriteria(StaffData.class);
				 staffData = (StaffData)criteria.add(Restrictions.like("staffId",staffId))
				 							    .uniqueResult();

				 session.delete(staffData);
			 }catch(HibernateException hExp){
				 hExp.printStackTrace();
				 throw new DataManagerException(hExp.getMessage(), hExp);
			 }catch(Exception exp){
				 throw new DataManagerException(exp.getMessage(),exp);
			 }
		}
		
		/*public void deleteStaffGroupRel(String staffId) throws DataManagerException{
			StaffGroupRelData staffGroupRelData = null;
			
			try {
				Session session = getSession();
				Criteria criteria = session.createCriteria(StaffGroupRelData.class)
										   .add(Restrictions.eq("staffId",staffId));
				
				List lstStaffGroupRelList = criteria.list();

				for(int i=0;i<lstStaffGroupRelList.size();i++){
					staffGroupRelData = (StaffGroupRelData)lstStaffGroupRelList.get(i);
					session.delete(staffGroupRelData);
				}
			} catch (HibernateException hExp) {
				hExp.printStackTrace();
				throw new DataManagerException(hExp.getMessage(),hExp);
			} catch (Exception exp) {
				exp.printStackTrace();
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}*/
		
		 /*public void deleteStaffAccessGroup(IStaffData staffData) throws DataManagerException{
			 IStaffGroupRelData staffGroupRelData = null;
			 try {
				Session session = getSession();
				Criteria criteria = session.createCriteria(StaffGroupRelData.class)
										   .add(Restrictions.eq("staffId",staffData.getStaffId()));
				//staffData = (StaffData)criteria.uniqueResult();
				List lstStaffGroupRelList = criteria.list();
				//List lstStaffGroupRelList = Arrays.asList(staffData.getStaffGroupRel().toArray()) ;
				System.out.println("%%%%%%%%%%% delete side the size is %%%%%%%%%"+lstStaffGroupRelList.size());
				for(int i=0;i<lstStaffGroupRelList.size();i++){
					staffGroupRelData = (StaffGroupRelData)lstStaffGroupRelList.get(i);
					System.out.println("&&&&& value of the i is :"+i+" groupid"+staffGroupRelData.getGroupId());
					session.delete(staffGroupRelData);
				}
			} catch(HibernateException hExp){
				 hExp.printStackTrace();
				 throw new DataManagerException(hExp.getMessage(), hExp);
			 }catch(Exception exp){
				 exp.printStackTrace();
				 throw new DataManagerException(exp.getMessage(),exp);
			 }
		 }*/
		
		/**
		 * @author  dhavalraval
		 * @param   staffData
		 * @throws  DataManagerException
		 * @purpose This method is generated to DeleteStaffAccessGroup.
		 */
		public void deleteStaffGroupRoleRelation(StaffData staffData) throws DataManagerException{
			StaffGroupRoleRelData staffGroupRoleRelData = null;
			
			try {
				Session session  = getSession();
				Criteria criteria = session.createCriteria(StaffGroupRoleRelData.class);
				criteria.add(Restrictions.eq("staffData",staffData));

				List lstStaffRoleRelList = criteria.list();
				for(int i=0;i<lstStaffRoleRelList.size();i++){
					staffGroupRoleRelData = (StaffGroupRoleRelData)lstStaffRoleRelList.get(i);
					session.delete(staffGroupRoleRelData);
				}
			} catch (HibernateException hExp) {
				hExp.printStackTrace();
				throw new DataManagerException(hExp.getMessage(),hExp);
			} catch (Exception exp) {
				exp.printStackTrace();
				throw new DataManagerException(exp.getMessage(),exp);
			}
		} 
		
		/*public void update(IStaffData istaffData,String staffId) throws DataManagerException{
				Session session = getSession();
//				Transaction tx = session.beginTransaction();
				StaffData staffData = null;
				
				try{
					Criteria criteria = session.createCriteria(StaffData.class);
					
					staffData = (StaffData)criteria.add(Restrictions.eq("staffId",staffId))
												   .uniqueResult();
					
					session.update(staffData);
					//session.flush();
					
				}catch(HibernateException hExp){
					hExp.printStackTrace();
					throw new DataManagerException(hExp.getMessage(),hExp);
				}catch(Exception exp){
					throw new DataManagerException(exp.getMessage(),exp);
				}
		}*/
		
		/**
		 * @author  dhavalraval
		 * @param   istaffData
		 * @param   statusChangeDate
		 * @throws  DataManagerException
		 * @purpose This method is generated to update StaffBasicDetais.
		 */
		public void updateBasicDetail(IStaffData istaffData,Timestamp statusChangeDate) throws DataManagerException{
				Session session = getSession();
				StaffData staffData = null;

				if(istaffData != null){
					try{
						Criteria criteria = session.createCriteria(StaffData.class);
						staffData = (StaffData)criteria.add(Restrictions.eq("staffId",istaffData.getStaffId()))
														  .uniqueResult();
						
						staffData.setName(istaffData.getName());
						staffData.setEmailAddress(istaffData.getEmailAddress());
						staffData.setPhone(istaffData.getPhone());
						staffData.setMobile(istaffData.getMobile());
						staffData.setLastModifiedDate(statusChangeDate);
						/*if(istaffData.getProfilePicture() != null){
							staffData.setProfilePicture(istaffData.getProfilePicture());
						}*/
						session.update(staffData);
						session.flush();
					}catch(HibernateException hExp){
						throw new DataManagerException(hExp.getMessage(),hExp);
					}catch(Exception exp){
						throw new DataManagerException(exp.getMessage(),exp);
					}
				}else{
					throw new DataManagerException("UpdateBasicDetail Failed");
				}
		}
		
		/**
		 * @author  dhavalraval
		 * @param   istaffData
		 * @param   statusChangeDate
		 * @throws  DataManagerException
		 * @purpose This method is generated to ChangeUserName of StaffData.
		 */
		public void changeUserName(IStaffData istaffData,Timestamp statusChangeDate) throws DataManagerException, DuplicateParameterFoundExcpetion{
				Session session = getSession();
				StaffData staffData = null;

				if(istaffData != null){
					try{
						Criteria criteria = session.createCriteria(StaffData.class);
						staffData = (StaffData)criteria.add(Restrictions.eq("staffId",istaffData.getStaffId()))
													   .uniqueResult();
						if(staffData.getUserName()!=null && !staffData.getUserName().equals(istaffData.getUserName().trim())){
							List list = getStaffData(istaffData.getUserName());
							if(list!=null && list.size()>0){
								throw new DuplicateParameterFoundExcpetion("Duplicate User Name.");
							}

						}
						staffData.setUserName(istaffData.getUserName().trim());
						staffData.setLastModifiedDate(statusChangeDate);
						session.update(staffData);
						session.flush();
					}catch(DuplicateParameterFoundExcpetion dpfExp){
						throw new DuplicateParameterFoundExcpetion("Duplicate User Name.");
					}catch(HibernateException hExp){
						throw new DataManagerException(hExp.getMessage(),hExp);
					}catch(Exception exp){
						throw new DataManagerException(exp.getMessage(),exp);
					}
				}else{
					throw new DataManagerException("ChangeUserName failed");
				}
		}
		
		/**
		 * @author  dhavalraval
		 * @param   staffId
		 * @param   oldPassword
		 * @param   newPassword
		 * @throws  DataManagerException
		 * @purpose This method is generated to change Password of StaffData.
		 */
		public void changePassword(long staffId, String oldPassword,String newPassword,Timestamp statusChangeDate, String newRecentPasswords) throws DataManagerException{
			Session session = getSession();			
			StaffData staffData = null;

			try{
				if(staffId != 0 && Strings.isNullOrBlank(oldPassword)== false && Strings.isNullOrBlank(newPassword) == false){
					Criteria criteria = session.createCriteria(StaffData.class);
					staffData = (StaffData)criteria.add(Restrictions.eq("staffId",staffId)).uniqueResult();

						staffData.setPassword(PasswordUtility.getEncryptedPassword(newPassword));
						staffData.setLastModifiedDate(statusChangeDate);
						staffData.setPasswordChangeDate(new Timestamp(new Date().getTime()));
						staffData.setRecentPasswords(newRecentPasswords);
						session.update(staffData);
						session.flush();
						staffData = null;
						criteria = null;
				}else{
					throw new DataManagerException("Invalid parameters");						
				}
			}
			catch(HibernateException hExp){
				throw new DataManagerException(hExp.getMessage(), hExp);
			}catch(Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}

		public void resetPassword(long staffId, String newPassword,Timestamp statusChangeDate) throws DataManagerException{
			Session session = getSession();
			StaffData staffData;

			try{
				if(staffId != 0 && Strings.isNullOrBlank(newPassword) == false){
					Criteria criteria = session.createCriteria(StaffData.class);
					staffData = (StaffData)criteria.add(Restrictions.eq("staffId",staffId)).uniqueResult();

					staffData.setPassword(PasswordUtility.getEncryptedPassword(newPassword));
					staffData.setLastModifiedDate(statusChangeDate);
					staffData.setPasswordChangeDate(statusChangeDate);
					session.update(staffData);
					session.flush();
				}else{
					throw new DataManagerException("Invalid parameters for reset password");
				}
			} catch(Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}

		public List<IStaffData> getStaffData(String userName) throws DataManagerException {
			EliteAssert.notNull(userName, "userName must be supplied");
			Session session = getSession();
			List<IStaffData> staffDataList = null;
			try{
				Criteria criteria = session.createCriteria(StaffData.class);
				return (List<IStaffData>)criteria.add(Restrictions.eq("userName",userName)).list();
			}catch(HibernateException hExp){
				throw new DataManagerException(hExp.getMessage(),hExp);
			}catch(Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
			}

		}
	
		public void updateLoginInfo(IStaffData staffData) throws DataManagerException {
			Session session = getSession();
			try{	
				Criteria criteria = session.createCriteria(StaffData.class);
				List<StaffData> existingStaffDataList = (List<StaffData>)criteria.add(Restrictions.eq("staffId",staffData.getStaffId())).list();
				if(existingStaffDataList != null){
					IStaffData existingStaffData = existingStaffDataList.get(0);
					existingStaffData.setLastLoginTime(staffData.getLastLoginTime());      
					session.update(existingStaffData);				
					session.flush();
				}
			}catch(HibernateException hExp){
				throw new DataManagerException(hExp.getMessage(), hExp);
			}catch(Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}
		
		/**
		 * @author  aneri.chavda
		 * @param   staffData
		 * @return
		 * @throws  DataManagerException
		 * @purpose This method is generated to update LastLoginDuration of StaffData at Logout time.
		 */
		public void updateLogoutInfo(IStaffData staffData) throws DataManagerException {
			Session session = getSession();
			try{	
				Criteria criteria = session.createCriteria(StaffData.class);
				List<StaffData> existingStaffDataList = (List<StaffData>)criteria.add(Restrictions.eq("staffId",staffData.getStaffId())).list();
				if(existingStaffDataList != null){
					IStaffData existingStaffData = existingStaffDataList.get(0);
						Timestamp lastLoginTime = staffData.getLastLoginTime();
						existingStaffData.setLastLoginDuration((System.currentTimeMillis() - lastLoginTime.getTime() ) / 1000 );
				session.update(existingStaffData);
				session.flush();
				}
			}catch(HibernateException hExp){
				throw new DataManagerException(hExp.getMessage(), hExp);
			}catch(Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}
				
		public Date getLastChangedPwdDate(String userName) throws DataManagerException{
			Session session = getSession();
			Date lastchangedPwdDate=null;
			Timestamp modifyDate=null;
			try{
				Criteria criteria = session.createCriteria(StaffData.class);
				List<IStaffData> staffDataList;
				staffDataList = (List<IStaffData>)criteria.add(Restrictions.eq("userName",userName)).list();
				
				for(IStaffData istaffData :staffDataList){
					modifyDate=istaffData.getPasswordChangeDate();
				}
				if(modifyDate == null){
					return null;
				}
				
				lastchangedPwdDate = new Date(modifyDate.getTime());
			}catch(Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
			}
			return lastchangedPwdDate;
			
		}

	@Override
	public void create(StaffProfilePictureData staffProfilePicture) throws DataManagerException {
		try{
			Session session = getSession();
			session.save(staffProfilePicture);
     	}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	@Override
	public void update(StaffProfilePictureData staffProfilePicture) throws DataManagerException{
		try{
			Session session = getSession();
			session.saveOrUpdate(staffProfilePicture);
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	@Override
	public StaffProfilePictureData getStaffProfilePicture(Long staffId) throws DataManagerException {
		StaffProfilePictureData staffProfilePicture=null;
		try {
			return (StaffProfilePictureData)getSession().get(StaffProfilePictureData.class,staffId);
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

}

