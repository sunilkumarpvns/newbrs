
/******************************************************************************

                         Ulticom, Inc. 
                Copyright 2005 All Rights Reserved. 

	These computer program listings and specifications, herein, 
	are the property of Ulticom, Inc. and shall not be 
	reproduced or copied or used in whole or in part as the basis 
	for manufacture or sale of items without written permission. 


@source   ulcm_hash.c

@ClearCase-version: $Revision:/main/sw9/2 $ 

@date     $Date:3-Oct-2006 11:34:06 $

@product  Signalware

@subsystem  WLAN Gateway

DISCLAIMER

SOURCE CODE EXAMPLES PROVIDED BY ULTICOM ARE ONLY INTENDED TO ASSIST IN THE DEVELOPMENT OF A WORKING SOFTWARE PROGRAM.  THE SOURCE CODE PROVIDED IS NOT WRITTEN AS AN EXAMPLE OF A RELEASED, PRODUCTION LEVEL APPLICATION, IT IS INTENDED ONLY TO DEMONSTRATE USAGE OF THE API FUNCTIONS USED HEREIN.
 
 ULTICOM, INC. PROVIDES THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE SOURCE CODE EXAMPLES, BOTH INDIVIDUALLY AND AS ONE OR MORE GROUPS, IS WITH YOU. SHOULD ANY PART OF THE SOURCE CODE EXAMPLES PROVE DEFECTIVE, YOU (AND NOT ULTICOM) ASSUME THE ENTIRE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION. IN NO EVENT SHALL ULTICOM BE LIABLE FOR DAMAGES OF ANY KIND, INCLUDING DIRECT, INDIRECT, INCIDENTAL, CONSEQUENTIAL, SPECIAL, EXEMPLARY OR PUNITIVE, EVEN IF IT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. ULTICOM DOES NOT WARRANT THAT THE CONTENTS OF THE SOURCE CODE EXAMPLES, WHETHER  INDIVIDUALLY OR AS ONE OR MORE GROUPS, WILL MEET YOUR REQUIREMENTS OR THAT THE SOURCE CODE EXAMPLES ARE ERROR FREE.

Ulticom shall have sole and exclusive ownership of all right, title, and interest in and to the Software, and all intellectual property rights associated therewith, including without limitation, rights to copyrights, trade secrets or know-how. This disclaimer agreement shall be governed and construed in accordance with the laws of the State of New Jersey.

Ulticom may make improvements and/or changes in source code examples at any time without notice. Ulticom has no continuing obligation to provide additional source code examples or revisions of previously provided examples.

Changes may be made periodically to the information in the source code examples; these changes may be reported, for the sample code included herein, in new editions of the product.

******************************************************************************/

#include <ulcm_hash.h>

//#define DEBUG 0

/*************************************************************************

FUNCTION
	void hash_init( hash_info_t * info )
DESCRIPTION
        Initialize Hash table.

INPUTS
        Arg:    info - pointer to hash_info structure.

OUTPUTS
        Return:	None

FEND
*************************************************************************/

void
hash_init( hash_info_t * info )
{
    U16             i;

    memset( info->table, 0, info->table_size * sizeof( *( info->table ) ) );
    memset( info->bucket, 0, info->bucket_size * sizeof( *( info->bucket ) ) );

    /* Build free bucket list */
    for ( i = 1; i < ( info->bucket_size - 1 ); i++ )
    {
	info->bucket[i].next = i + 1;
    }
    info->bucket[info->bucket_size - 1].next = 0;
    info->first_bucket_free = 1;
}


/*************************************************************************

FUNCTION
	U16 hash_add_entry( hash_info_t * info, U32 hash_key, U16 lnk_index )

DESCRIPTION
       	Add entry in hash table. 

INPUTS
        Arg:    info - pointer to hash_info structure.
		hash_key - hash key
		lnk_index - link index

OUTPUTS
        Return:	0 - if no more buckets available 
		1 - Success

FEND
*************************************************************************/
U16
hash_add_entry( hash_info_t * info, U32 hash_key, U16 lnk_index )
{
    U16             table_index = info->hash_function( hash_key );
    U16             bucket_index;
#ifdef DEBUG
    printf("hash_add_entry: key=%d, hashed to index=%d, freespot=%d\n", hash_key, table_index, lnk_index);
#endif

    if ( table_index > info->table_size )
    {
#ifdef DEBUG
	printf("hash_add_entry: leave 1\n");
#endif
	return 0;
    }

    if ( !info->first_bucket_free )
    {
#ifdef DEBUG
	printf("hash_add_entry: leave 2\n");
#endif
	return 0;
    }
    bucket_index = info->first_bucket_free;
    info->first_bucket_free = info->bucket[bucket_index].next;

    info->bucket[bucket_index].next = info->table[table_index];
    info->table[table_index] = bucket_index;

    info->bucket[bucket_index].hash_key = hash_key;
    info->bucket[bucket_index].lnk_index = lnk_index;

#ifdef DEBUG
    printf("hash_add_entry: leave 3\n");
#endif
    return 1;
}

/*************************************************************************

FUNCTION
	U16 hash_remove_entry( hash_info_t * info, U32 hash_key )

DESCRIPTION
       	Remove entry from hash table. 

INPUTS
        Arg:    info - pointer to hash_info structure.
		hash_key - hash key

OUTPUTS
        Return:	0 - if entry not foundn 
		>0 - corresponding index 
FEND
*************************************************************************/
U16
hash_remove_entry( hash_info_t * info, U32 hash_key )
{
    U16             table_index = info->hash_function( hash_key );
    U16             bucket_index;
    U16             prev_bucket_index = 0;
#ifdef DEBUG
    printf("hash_remove_entry: key=%d, hashed to index=%d\n", hash_key, table_index);
#endif

    if ( table_index > info->table_size )
    {
#ifdef DEBUG
	printf("hash_remove_entry: leave 1\n");
#endif
	return 0;
    }
    bucket_index = info->table[table_index];

    while ( bucket_index )
    {
	if ( info->bucket[bucket_index].hash_key == hash_key )
	{
	    if ( prev_bucket_index )
	    {
		info->bucket[prev_bucket_index].next = info->bucket[bucket_index].next;
	    }
	    else
	    {
		info->table[table_index] = info->bucket[bucket_index].next;
	    }
	    info->bucket[bucket_index].next = info->first_bucket_free;
	    info->first_bucket_free = bucket_index;
#ifdef DEBUG
	    printf("hash_remove_entry: leave, returning index=%d\n", info->bucket[bucket_index].lnk_index);
#endif
	    return info->bucket[bucket_index].lnk_index;
	}

	prev_bucket_index = bucket_index;
	bucket_index = info->bucket[bucket_index].next;
    }
#ifdef DEBUG
    printf("hash_remove_entry: leave 2\n");
#endif
    return 0;
}

/*************************************************************************

FUNCTION
	U16 hash_get_entry( hash_info_t * info, U32 hash_key )	 

DESCRIPTION
       	Get entry in hash table. 

INPUTS
        Arg:    info - pointer to hash_info structure.
		hash_key - hash key

OUTPUTS
        Return:	0 - if entry not foundn 
		>0 - corresponding index 

FEND
*************************************************************************/

U16
hash_get_entry( hash_info_t * info, U32 hash_key )
{
    U16             table_index = info->hash_function( hash_key );
    U16             bucket_index;

#ifdef DEBUG
    printf("hash_get_entry: key=%d, hashed to index=%d\n", hash_key, table_index);
#endif
    if ( table_index > info->table_size )
    {
#ifdef DEBUG
	printf("hash_get_entry: leave 1\n");
#endif
	return 0;
    }
    bucket_index = info->table[table_index];

    while ( bucket_index )
    {
	if ( info->bucket[bucket_index].hash_key == hash_key )
	{
#ifdef DEBUG
	    printf("hash_get_entry: leave, returning index=%d\n", info->bucket[bucket_index].lnk_index);
#endif
	    return info->bucket[bucket_index].lnk_index;
	}

	bucket_index = info->bucket[bucket_index].next;
    }
#ifdef DEBUG
    printf("hash_get_entry: leave 2\n");
#endif
    return 0;
}

