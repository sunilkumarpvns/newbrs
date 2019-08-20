package com.elitecore.netvertexsm.hibernate.servergroup;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servergroup.ServerInstanceGroupDataManager;
import com.elitecore.netvertexsm.datamanager.servergroup.data.ServerInstanceGroupData;
import com.elitecore.netvertexsm.datamanager.servergroup.data.ServerInstanceGroupRelationData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.web.servergroup.form.ServerInstanceGroupRelationForm;

/**
 * Created by aditya on 11/5/16.
 */
public class HServerInstnaceGroupDataManager extends HBaseDataManager implements ServerInstanceGroupDataManager {
	
    @Override
    public PageList search(ServerInstanceGroupData serverInstanceGroupData, int requiredPageNo, Integer pageSize,String staffBelongingGroups) throws DataManagerException {
        PageList pageList = null;
        try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(ServerInstanceGroupData.class).addOrder(Order.asc("name"));
            if(serverInstanceGroupData.getName() != null && serverInstanceGroupData.getName().length() > 0){
                criteria.add(Restrictions.ilike("name", serverInstanceGroupData.getName(), MatchMode.ANYWHERE));
            }
            int totalItems = criteria.list().size();
            criteria.setFirstResult(((requiredPageNo - 1) * pageSize));
            if(pageSize > 0){
                criteria.setMaxResults(pageSize);
            }
            
            long totalPages = (long) Math.ceil(totalItems / pageSize);
            if(totalItems % pageSize == 0){
                totalPages -= 1;
            }
            pageList = new PageList(filterGroups(criteria.list(), totalItems, staffBelongingGroups), requiredPageNo, totalPages, totalItems);
            
        }catch(HibernateException hExe){
            throw new DataManagerException(hExe.getMessage(), hExe);
        }catch(Exception exp){
            throw new DataManagerException(exp.getMessage(), exp);
        }
        return pageList;
    }
    
    /**
     * This Method will return the list of ServerInstanceGroup in which staff is belongs
     * @param groupDataList
     * @param totalItems
     * @param staffBelongingGroups
     * @return List of server groups
     * @author Dhyani.Raval
     */
    private List filterGroups(List groupDataList,int totalItems ,String staffBelongingGroups ) {
    	List serverInstanceGroupDatas = Collectionz.newArrayList();
    	if(Collectionz.isNullOrEmpty(groupDataList)) {
    		 return serverInstanceGroupDatas;
    	}
        List<String> staffBelongingGroupsAsList = Strings.splitter(',').trimTokens().split(staffBelongingGroups);
        for(int i =0; i<totalItems; i++) { 
        	ServerInstanceGroupData instanceGroupData = (ServerInstanceGroupData) groupDataList.get(i);
        	List<String> accessGroupList = Strings.splitter(',').trimTokens().split(instanceGroupData.getAccessGroups());
        	for(String groupId : accessGroupList) {
        		if(staffBelongingGroupsAsList.contains(groupId)){
        			serverInstanceGroupDatas.add(instanceGroupData);
        			break;
    	    	}
        	}
        }
		return serverInstanceGroupDatas;
    }

    @Override
    public void create(ServerInstanceGroupData serverInstanceGroupData) throws DataManagerException {
        try{
            Session session = getSession();
            session.save(serverInstanceGroupData);
            session.flush();
        }catch(ConstraintViolationException hExp){
            throw hExp;
        }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
            throw new DataManagerException(exp.getMessage(),exp);
        }



    }

    @Override
    public void update(ServerInstanceGroupData serverInstanceGroupData) throws DataManagerException {
        try{
            Session session = getSession();
            session.update(serverInstanceGroupData);
            session.flush();
        }catch(ConstraintViolationException hExp){
            throw hExp;
        }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
            throw new DataManagerException(exp.getMessage(),exp);
        }


    }

    @Override
    public ServerInstanceGroupData getServerIntanceGroupData(String id) throws DataManagerException {
        NetworkData networkData=null;
        try{
            Session session = getSession();
            return (ServerInstanceGroupData) session.get(ServerInstanceGroupData.class,id);
        }catch(HibernateException hExp){
            hExp.printStackTrace();
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
            exp.printStackTrace();
            throw new DataManagerException(exp.getMessage(),exp);
        }
    }

    @Override
    public void delete(String ids) throws DataManagerException {
        try{
            Session session = getSession();
            List list = session.createCriteria(ServerInstanceGroupData.class).add(Restrictions.eq("id", ids)).list();
            deleteObjectList(list,session);
        }catch(HibernateException hExp){
            hExp.printStackTrace();
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
            exp.printStackTrace();
            throw new DataManagerException(exp.getMessage(),exp);
        }


    }

	@Override
	public List getServerInstanceGroupRelationDatas() throws DataManagerException{
		Query query= null;
		try{
			
			String QUERY = "SELECT A.SERVER_WEIGHTAGE as serverWeightage, C.NAME as serverName, C.DESCRIPTION as description,C.ADMINHOST as adminHost,C.ADMINPORT as adminPort,C.NETSERVERID as netServerId,B.ID as serverGroupId from TBLM_NET_SERVER_GROUP_REL A INNER JOIN TBLM_SERVER_INSTANCE_GROUP B ON A.SERVER_INSTANCE_GROUP_ID = B.ID INNER JOIN TBLMNETSERVERINSTANCE C ON C.NETSERVERID = A.NETSERVER_INSTANCE_ID ";
			Session session = getSession();
			query = session.createSQLQuery(QUERY).addScalar("serverWeightage",StringType.INSTANCE).addScalar("serverName",StringType.INSTANCE).addScalar("description",StringType.INSTANCE).addScalar("adminHost",StringType.INSTANCE).addScalar("adminPort",IntegerType.INSTANCE).addScalar("netServerId",StringType.INSTANCE).addScalar("serverGroupId",StringType.INSTANCE).setResultTransformer(Transformers.aliasToBean(ServerInstanceGroupRelationForm.class));
		}catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
            throw new DataManagerException(exp.getMessage(),exp);
        }
		return query.list();
	}

	@Override
	public List<ServerInstanceGroupRelationData> getServerInstanceGroupRelationDatasBy(String serverGroupId) throws DataManagerException{
		try{
			Session session = getSession();
            Criteria criteria = session.createCriteria(ServerInstanceGroupRelationData.class);
            criteria.add(Restrictions.eq("serverInstanceGroupId", serverGroupId));
            return criteria.list();
            
		}catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
            throw new DataManagerException(exp.getMessage(),exp);
        }
	}
	
	@Override
	public ServerInstanceGroupRelationData getServerInstanceRelationDatasBy(String serverInstanceId) throws DataManagerException{
		try{
			Session session = getSession();
            Criteria criteria = session.createCriteria(ServerInstanceGroupRelationData.class);
            criteria.add(Restrictions.eq("netServerInstanceId", serverInstanceId));
            
            return (ServerInstanceGroupRelationData) criteria.list().get(0);
            
		}catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
            throw new DataManagerException(exp.getMessage(),exp);
        }
	}
	
	@Override
	public void swapInstances(String serverInstanceGroupId)throws DataManagerException {
		try{
			List<ServerInstanceGroupRelationData> serverInstanceGroupRelationDatas = getServerInstanceGroupRelationDatasBy(serverInstanceGroupId);
			if(Collectionz.isNullOrEmpty(serverInstanceGroupRelationDatas) == false && serverInstanceGroupRelationDatas.size() == 2){
				for(ServerInstanceGroupRelationData data : serverInstanceGroupRelationDatas){
					if(data.getServerWeightage() == CommonConstants.PRIMARY_INSTANCE){
						data.setServerWeightage(CommonConstants.SECONDARY_INSATNCE);
						Session session = getSession();
						session.update(data);
						session.flush();
					}else{
						data.setServerWeightage(CommonConstants.PRIMARY_INSTANCE);
						Session session = getSession();
						session.update(data);
						session.flush();
					}
				}
			}
		}catch(ConstraintViolationException hExp){
			throw hExp;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}

	}
	
	@Override
	public void manageOrder(String[] serverInstanceGroupId)throws DataManagerException {
		try{
			Session session = getSession();
            Criteria criteria = session.createCriteria(ServerInstanceGroupData.class);
            criteria.add(Restrictions.in("id", serverInstanceGroupId));
            List<ServerInstanceGroupData> serverInstanceGroupDatas = criteria.list();
            
            if(serverInstanceGroupId != null){
				for(int i=0;i<serverInstanceGroupId.length;i++){
					String id = serverInstanceGroupId[i];
					for(int j=0;j<serverInstanceGroupDatas.size();j++){
						ServerInstanceGroupData serverInstanceGroupData = serverInstanceGroupDatas.get(j);
						if(serverInstanceGroupData.getId().equals(id)){
							serverInstanceGroupData.setOrderNo(i+1);
							session.update(serverInstanceGroupData);
							break;
						}
					}
				}
			}
		}catch(ConstraintViolationException hExp){
			throw hExp;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}

	}

	@Override
	public Integer getMaxOrder() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ServerInstanceGroupData.class);
			criteria.setProjection(Projections.max("orderNo"));
			Integer maxAge = (Integer)criteria.uniqueResult();
			return maxAge;
		}catch(ConstraintViolationException hExp){
			throw hExp;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}


}
