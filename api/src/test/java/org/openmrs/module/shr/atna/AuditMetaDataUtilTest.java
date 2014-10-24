package org.openmrs.module.shr.atna;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.shr.atna.configuration.AtnaConfiguration;
import org.openmrs.module.shr.atna.util.AuditMetaDataUtil;

/**
 * Test the ATNA service
 * @author justi_000
 *
 */
public class AuditMetaDataUtilTest {

	// Get the log
	private final Log log = LogFactory.getLog(this.getClass());
	
	// Meta data utility
	private AuditMetaDataUtil m_metaDataUtil = AuditMetaDataUtil.getInstance();
	// Configuration
	private AtnaConfiguration m_configuration = AtnaConfiguration.getInstance();
	
	

}
