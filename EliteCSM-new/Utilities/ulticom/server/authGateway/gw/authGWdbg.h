/******************************************************************************

                         Ulticom, Inc. 
                Copyright 2005 All Rights Reserved. 

	These computer program listings and specifications, herein, 
	are the property of Ulticom, Inc. and shall not be 
	reproduced or copied or used in whole or in part as the basis 
	for manufacture or sale of items without written permission. 


@source  authGWdbg.h 

@ClearCase-version: $Revision:/main/sw9/1 $ 

@date     $Date:5-Apr-2005 11:05:44 $

@product  Signalware

@subsystem  WLAN Gateway

DISCLAIMER

SOURCE CODE EXAMPLES PROVIDED BY ULTICOM ARE ONLY INTENDED TO ASSIST IN THE DEVELOPMENT OF A WORKING SOFTWARE PROGRAM.  THE SOURCE CODE PROVIDED IS NOT WRITTEN AS AN EXAMPLE OF A RELEASED, PRODUCTION LEVEL APPLICATION, IT IS INTENDED ONLY TO DEMONSTRATE USAGE OF THE API FUNCTIONS USED HEREIN.
 
 ULTICOM, INC. PROVIDES THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, IS WITH YOU. SHOULD ANY PART OF THE SOURCE CODE EXAMPLES PROVE DEFECTIVE, YOU (AND NOT ULTICOM) ASSUME THE ENTIRE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION. IN NO EVENT SHALL ULTICOM BE LIABLE FOR DAMAGES OF ANY KIND, INCLUDING DIRECT, INDIRECT, INCIDENTAL, CONSEQUENTIAL, SPECIAL, EXEMPLARY OR PUNITIVE, EVEN IF IT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. ULTICOM DOES NOT WARRANT THAT THE CONTENTS OF THE SOURCE CODE EXAMPLES, WHETHER  INDIVIDUALLY OR AS ONE OR MORE GROUPS, WILL MEET YOUR REQUIREMENTS OR THAT THE SOURCE CODE EXAMPLES ARE ERROR FREE.

Ulticom shall have sole and exclusive ownership of all right, title, and interest in and to the Software, and all intellectual property rights associated therewith, including without limitation, rights to copyrights, trade secrets or know-how. This disclaimer agreement shall be governed and construed in accordance with the laws of the State of New Jersey.

Ulticom may make improvements and/or changes in source code examples at any time without notice. Ulticom has no continuing obligation to provide additional source code examples or revisions of previously provided examples.

Changes may be made periodically to the information in the source code examples; these changes may be reported, for the sample code included herein, in new editions of the product.

******************************************************************************/


#ifndef	_mg_dbg_h_
#define	_mg_dbg_h_
#include <stdio.h>
/**************** constants *********************************************/

#define	DBG_MASK_ER		1	/* error type           */
#define	DBG_MASK_L1		2	/* significant event    */
#define	DBG_MASK_L2		4	/* less significant     */
#define	DBG_MASK_L3		8
#define	DBG_MASK_L4		0x10	/* least significant    */

#define DBG_MAX_NAME		24

/*SUBTITLE dbg_ipc_t - debug IPC message buffer definitions		*/
/*************************************************************************
**                                                                      **
** Name:                dbg_ipc_t        				**
**                                                                      **
** Description:         This structure is used only for casting of	**
**			an existing IPC buffer for debug trace.		**
**                                                                      **
** Restrictions:        None						**
**                                                                      **
*************************************************************************/
typedef struct
{
    Header_t        hdr;
    U8              data[1];
#if defined(ALIGN_64BIT)
    U32             pad_1;
#endif
}
dbg_ipc_t;



/*SUBTITLE dbg_trace_ent_t - debug trace entry definition		*/
/*************************************************************************
**                                                                      **
** Name:                dbg_trace_ent_t        				**
**                                                                      **
** Description:                                                         **
**                                                                      **
** Restrictions:        None						**
**                                                                      **
*************************************************************************/
typedef struct
{
    omni_long( tv_sec, pad_1 );	/* seconds since 1/1/70         */
    omni_long( tv_usec, pad_2 );	/* micro-seconds                */

    U16             seq_nbr;	/* trace sequence number        */
    U8              type;	/* trace type                   */

#define	DBG_TP_ASCII		1	/* null terminated ascii text   */
#define	DBG_TP_SEND_IPC		2	/* send IPC type                */
#define	DBG_TP_RECV_IPC		3	/* receive IPC type             */
#define	DBG_TP_BIN		4	/* binary data type             */
#if defined(ALIGN_64BIT)
    U32             pad_3;
#endif
                    omni_long( msg_type, pad_4 );	/* for IPC types                */
    int             primitive;	/* for binary type              */
    int             msg_size;	/* actual data size             */
    char            proc_name[MAXlogicalNAME + 1];	/* IPC orig/dest name   */

#define	DBG_TRACE_DATA_SZ	128

    U8              data[DBG_TRACE_DATA_SZ + 1];
#if defined(ALIGN_64BIT)
    U32             pad_5;
#endif
}
dbg_trace_ent_t;



/*SUBTITLE dbg_errlog_ent_t - debug error type og entry			*/
/*************************************************************************
**                                                                      **
** Name:                dbg_errlog_ent_t        			**
**                                                                      **
** Description:                                                         **
**                                                                      **
** Restrictions:        None						**
**                                                                      **
*************************************************************************/
typedef struct
{
    omni_long( tv_sec, pad_1 );	/* seconds since 1/1/70         */
    omni_long( tv_usec, pad_2 );	/* micro-seconds                */
    U8              err_data_type;	/* type of data                 */

#define	DBG_ERR_DATA_ASC	1
#define	DBG_ERR_DATA_BIN	2

    U8              data_size;	/* valid only if ERR_DATA_BIN   */

    U16             seq_nbr;	/* error log sequence number    */

#define DBG_ERR_DATA_SZ	100

    U8              data[DBG_ERR_DATA_SZ];	/* null terminated ASCII string */
    /* or binary data               */
}
dbg_errlog_ent_t;


/*SUBTITLE dbg_trace_t - debug trace structure     			*/
/*************************************************************************
**                                                                      **
** Name:                dbg_trace_t 	      				**
**                                                                      **
** Description:                                                         **
**                                                                      **
** Restrictions:        None						**
**                                                                      **
*************************************************************************/

#define DBG_TRACE_ENTRIES	500

typedef struct
{
    U8              wrapped;	/* TRUE if trace buffer wrapped */
    U16             index;	/* trace entry index            */
#if defined(ALIGN_64BIT)
    U32             pad_1;
#endif
    dbg_trace_ent_t ent[DBG_TRACE_ENTRIES + 1];

}
dbg_trace_t;



/*SUBTITLE dbg_errlog_t - debug error type log				*/
/*************************************************************************
**                                                                      **
** Name:                dbg_errlog_t       				**
**                                                                      **
** Description:                                                         **
**                                                                      **
** Restrictions:        None						**
**                                                                      **
*************************************************************************/

#define DBG_ERRLOG_ENTRIES	60

typedef struct
{
    U8              wrapped;	/* TRUE if log buffer wrapped   */
    U16             index;	/* error log entry index        */
#if defined(ALIGN_64BIT)
    U32             pad_1;
#endif
    dbg_errlog_ent_t ent[DBG_ERRLOG_ENTRIES + 1];

}
dbg_errlog_t;



/*SUBTITLE dbg_t - debug structure					*/
/*************************************************************************
**                                                                      **
** Name:                dbg_t		      				**
**                                                                      **
** Description:         this structure defines the global shared memory	**
**			debug structure					**
**                                                                      **
** Restrictions:        None						**
**                                                                      **
*************************************************************************/

typedef struct
{
    U8              trace_enabled;	/* TRUE/FALSE                   */
    U32             trace_freeze;	/* 0 - if no trace freeze.      */
    /* otherwise, it contains the number for */
    /* DBG_DISP(DBG_MASK_ER,err_number..)   */
    /* err_number.  The trace will be frozen */
    /* if that err_number matches this      */
    /* field.  The number is settable by    */
    /* gmapomd command.                     */

    char            dbg_buff[200];	/* buffer for sprintf()         */

    U32             trace_seq_nbr;	/* trace/errlog sequence number */
#if defined(ALIGN_64BIT)
    U32             pad_1;
#endif
    dbg_trace_t     trace;	/* event trace buffer           */
    dbg_errlog_t    errlog;	/* error event trace buffer     */
    FILE           *trace_file;
#define MAX_TRACE_FILENAME 1024
    char            traceFilename[MAX_TRACE_FILENAME];
    int             newTraceFile;
    int             dbgMask;

}
dbg_t;




/*SUBTITLE dbg_field_t - ascii string field (token) definition		*/
/*************************************************************************
**                                                                      **
** Name:                dbg_field_t       				**
**                                                                      **
** Description:         Defines the structure of one taken in a		**
**                      MML command					**
**                                                                      **
** Restrictions:        						**
**                                                                      **
*************************************************************************/
typedef struct
{

    U16             len;	/* field length                 */
    char            delimiter;	/* field delimiter character    */

#define	DBG_MAX_FLD_SZ	80

    char            data[DBG_MAX_FLD_SZ];	/* field content                */
#if defined(ALIGN_64BIT)
    U32             pad_1;
#endif
}
dbg_field_t;



/**************** function prototypes ***********************************/

void            mg_dbg_init( dbg_t * pdbg, char *pname, int argc, char *argv[], FILE * trace_file, int *error, char *errorReport );
void            mg_dbg_disp( U32 mask, U32 err_number, char *format, ... );
void            mg_dbg_trace_execution( char *format, ... );
void            mg_dbg_trace_ipc( dbg_ipc_t * pipc, U8 type, unsigned int msg_size );
void            mg_dbg_trace_bin( char *pdata, int primitive, int msg_size, char *phdr, ... );
int             mg_dbg_process_dbg_cmd( char *pcmd );

void            mg_dbg_set_dbg_mask( int argc, char *argv[] );
void            mg_dbg_error_log( char *buff );
dbg_trace_ent_t *mg_dbg_trace_get_entry( void );
void            mg_dbg_gettimeofday( struct timeval *pcurr_time );

int             mg_dbg_get_token( dbg_field_t * pfld, char *pstr, char *ds );
void            mg_dbg_dump_trace_data( U8 * pU8, int size, int spaces );
void            mg_dbg_hex_dump_a_line( U8 * pdata, int size );
void            mg_dbg_hex_dump( char *phdr, U8 * pdata, int size );
void            mg_dbg_hex_to_asc( U8 * pdata, int size );
int             mg_dbg_hex_all_zeros( U8 * pdata, int size );
int             mg_dbg_copym( char *s1, char *s2, U16 size );
int             mg_dbg_cmp( char *s1, char *s2, U16 size );
int             mg_dbg_cmp_ignore_case( char *s1, char *s2, U16 size );
long            mg_dbg_gethex( char *pc );
U32             mg_dbg_getdec( char *pc );
int             mg_dbg_all_digit( char *pc, U16 size );
int             mg_dbg_get_dpc( dbg_field_t * fld, char *pcmd, char *pdelimiter );
void            mg_dbg_disp_pc( U32 pc );
int             mg_DbgLevelEqual( U32 mask );


#define DBG_DISP	mg_dbg_disp	/* compatibility with other version */
#endif
