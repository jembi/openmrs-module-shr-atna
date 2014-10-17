package org.openmrs.module.shr.atna.api.impl;

import org.marc.shic.atna.AtnaCommunicator;
import org.marc.shic.atna.AuditMetaData;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.shr.atna.api.AtnaService;
import org.openmrs.module.shr.atna.configuration.AtnaConfiguration;
import org.openmrs.module.shr.atna.util.AuditMetaDataUtil;

/**
 * Implementation of the AtnaService
 * @author justi_000
 *
 */
public class AtnaServiceImpl extends BaseOpenmrsService implements AtnaService {

	// Config tool
	private final AtnaConfiguration m_configuration = AtnaConfiguration.getInstance();
	// Communicator
	private final AtnaCommunicator m_communicator = new AtnaCommunicator(this.m_configuration.createShicConfiguration());
	private final AuditMetaDataUtil m_auditUtil = AuditMetaDataUtil.getInstance();
	
	/**
	 * Send an audit
	 */
	public void sendAudit(AuditMetaData auditData) {
		this.m_communicator.enqueueMessage(auditData, this.m_communicator.getConfiguration()); 
	}
	

}
