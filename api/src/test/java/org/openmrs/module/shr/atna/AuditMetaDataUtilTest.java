package org.openmrs.module.shr.atna;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.marc.shic.atna.AuditActorType;
import org.marc.shic.atna.AuditEventType;
import org.marc.shic.atna.AuditMetaData;
import org.marc.shic.atna.exceptions.AtnaMessageParseException;
import org.marc.shic.atna.messages.AuditUtility;
import org.marc.shic.core.configuration.IheConfiguration;
import org.marc.shic.core.exceptions.NtpCommunicationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.shr.atna.api.AtnaService;
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
	// Create IHE configuration
	private IheConfiguration m_iheConfiguration = this.m_configuration.createShicConfiguration();
	
	/**
	 * Test the creation of a simple ApplicationStart message
	 */
	@Test
	public void testApplicationStart() {
		
		// Create an audit message
		AuditMetaData auditMessage = new AuditMetaData(AuditEventType.ApplicationActivityStart);
		auditMessage.addActor(this.m_metaDataUtil.getOpenSHRActor(AuditActorType.Application, null));

		try {
			log.error(AuditUtility.generateAuditPayload(auditMessage, this.m_iheConfiguration));
		} catch (Exception e) {
			log.error(e);
		}
		
	}

}
