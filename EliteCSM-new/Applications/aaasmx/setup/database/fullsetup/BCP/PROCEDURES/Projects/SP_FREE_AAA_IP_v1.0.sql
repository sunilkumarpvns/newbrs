CREATE OR REPLACE PROCEDURE SP_FREE_AAA_IP(                                                       
    I_IPPOOLID IN OUT TBLMIPPOOLDETAIL.IPPOOLID%TYPE,                           
    O_SERIALNUMBER OUT TBLMIPPOOLDETAIL.SERIALNUMBER%TYPE,                      
    O_IPADDRESS OUT TBLMIPPOOLDETAIL.IPADDRESS%TYPE,                            
    I_CALLING_STATION_ID TBLMIPPOOLDETAIL.CALLING_STATION_ID%TYPE,              
    I_USER_IDENTITY TBLMIPPOOLDETAIL.USER_IDENTITY%TYPE,                        
    I_NAS_IP_ADDRESS TBLMIPPOOLDETAIL.NAS_IP_ADDRESS%TYPE,                      
    I_NAS_ID TBLMIPPOOLDETAIL.NAS_ID%TYPE)                                      
AS                                                                              
      /*  ELITECSM IP POOL MODULE                                               
          ELITECORE TECHNOLOGIES PVT. LTD. */                                   
                                                                               
  CURSOR AAA_IP                                                                 
  IS                                                                            
    SELECT IPPOOLID,                                                            
      SERIALNUMBER,                                                             
      IPADDRESS                                                                 
    FROM TBLMIPPOOLDETAIL                                                       
    WHERE IPPOOLID = I_IPPOOLID                                                 
    AND ( RESERVED = 'N'                                                        
    OR (RESERVED   ='Y'                                                         
    AND ASSIGNED   ='N'                                                         
    AND LAST_UPDATED_TIME + INTERVAL '5' MINUTE < SYSTIMESTAMP))                
    FOR UPDATE SKIP LOCKED;                                                     
                                                                                
  /*CURSOR C_AAA_IP                                                             
  IS                                                                            
    SELECT IPPOOLID,                                                            
      SERIALNUMBER,                                                             
      IPADDRESS                                                                 
    FROM TBLMIPPOOLDETAIL                                                       
    WHERE CALLING_STATION_ID=I_CALLING_STATION_ID                               
    AND IPPOOLID   = I_IPPOOLID FOR UPDATE SKIP LOCKED;                         

  */                                                                            
BEGIN                                                                           
  OPEN AAA_IP;                                                                  
  FETCH AAA_IP INTO I_IPPOOLID,O_SERIALNUMBER,O_IPADDRESS;                      
  IF O_IPADDRESS IS NOT NULL THEN                                               
      DBMS_OUTPUT.PUT_LINE(I_IPPOOLID||' , '||O_SERIALNUMBER||' ,'||O_IPADDRESS);                                                                               
                                                                                
                                                                                
      UPDATE TBLMIPPOOLDETAIL A                                                 
      SET A.RESERVED         = 'Y',                                             
        A.USER_IDENTITY      = I_USER_IDENTITY,                                 
        A.LAST_UPDATED_TIME  = SYSTIMESTAMP,                                    
        A.NAS_IP_ADDRESS     = I_NAS_IP_ADDRESS ,                               
        A.NAS_ID             = I_NAS_ID ,                                       
        A.CALLING_STATION_ID = I_CALLING_STATION_ID                             
      WHERE A.IPPOOLID       = I_IPPOOLID                                       
      AND A.SERIALNUMBER     = O_SERIALNUMBER;                                  
      COMMIT WORK WRITE NOWAIT;                                                 
/*                                                                              
  ELSE                                                                          
      OPEN C_AAA_IP;                                                            

      FETCH C_AAA_IP INTO I_IPPOOLID,O_SERIALNUMBER,O_IPADDRESS;                
      DBMS_OUTPUT.PUT_LINE(I_IPPOOLID||' , '||O_SERIALNUMBER||' ,'||O_IPADDRESS);                                                                               
                                                                                
                                                                                
      UPDATE TBLMIPPOOLDETAIL A                                                 
      SET A.RESERVED         = 'Y',                                             
        A.USER_IDENTITY      = I_USER_IDENTITY,                                 
        A.LAST_UPDATED_TIME  = SYSTIMESTAMP,                                    
        A.NAS_IP_ADDRESS     = I_NAS_IP_ADDRESS ,                               
        A.NAS_ID             = I_NAS_ID ,                                       
        A.CALLING_STATION_ID = I_CALLING_STATION_ID                             
      WHERE A.IPPOOLID       = I_IPPOOLID                                       
      AND A.SERIALNUMBER     = O_SERIALNUMBER;                                  
      COMMIT WORK WRITE NOWAIT;                                                 
                                                                                
      CLOSE C_AAA_IP;                                                           
 */                                                                             
  END IF;                                                                       
  CLOSE AAA_IP;                                                                 
EXCEPTION                                                                       
WHEN OTHERS THEN                                                                
  DBMS_OUTPUT.PUT_LINE(SQLCODE||':-'||SQLERRM);                                 
END;                                                                            
/