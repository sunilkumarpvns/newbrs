/**
 *   All the functionality related to model(i.e. Database) will reside in this package.
 *   The package level configuration like Sequence generator will mentioned here which 
 *   will be global & can be used throughout the application 
 */

@GenericGenerator(strategy = "com.elitecore.corenetvertex.pkg.EliteSequenceGenerator", name = "eliteSequenceGenerator")
package com.elitecore.corenetvertex.pkg;
import org.hibernate.annotations.GenericGenerator;


