package org.openmrs.module.shr.atna.configuration;

import org.marc.everest.formatters.FormatterUtil;
import org.marc.shic.core.DomainIdentifier;
import org.marc.shic.core.configuration.IheActorConfiguration;
import org.marc.shic.core.configuration.IheActorType;
import org.marc.shic.core.configuration.IheAffinityDomainConfiguration;
import org.marc.shic.core.configuration.IheConfiguration;
import org.marc.shic.core.configuration.IheIdentification;
import org.marc.shic.core.configuration.JKSStoreInformation;
import org.openmrs.GlobalProperty;
import org.openmrs.ImplementationId;
import org.openmrs.api.context.Context;

/**
 * Shared Health Integration Components (SHIC) configuration utility
 * @author justi_000
 *
 */
public final class AtnaConfiguration {

	// Lock object
	private static final Object s_lockObject = new Object();
	// Singleton
	private static AtnaConfiguration s_instance;
	
	private static final String PROP_NAME_JKSTRUST_STORE = "shr-atna.security.trustStore";
	private static final String PROP_NAME_JKSTRUST_PASS = "shr-atna.security.trustStorePassword";
	private static final String PROP_NAME_JKSKEY_STORE = "shr-atna.security.keyStore";
	private static final String PROP_NAME_JKSKEY_PASS = "shr-atna.security.keyStorePassword";
	private static final String PROP_SHR_ROOT = "shr.id.root";
    public static final String PROP_EPID_ROOT = "shr.id.epidRoot";
	private static final String PROP_ECID_ROOT = "shr.id.ecidRoot";
	private static final String PROP_NAME_AR_ENDPOINT = "shr-atna.auditRepository.endPoint";

	private final String m_trustStoreDefault = "";
	private final String m_trustStorePasswordDefault = "";
	private final String m_keyStoreDefault = "";
	private final String m_keyStorePasswordDefault = "";
	private final String m_shrRootDefault = "1.2.3.4.5.6";
	private final String m_shrEcidRootDefault = "";
	private final String m_shrEpidRootDefault = "";
	private final String m_arEndpointDefault = "udp://127.0.0.1:514";
	
	/**
	 * Shic configuration utility
	 */
	private AtnaConfiguration()
	{
		
	}
	
	/**
	 * Get the instance of the configuration utility
	 * @return
	 */
	public static AtnaConfiguration getInstance()
	{
		if(s_instance == null)
			synchronized (s_lockObject) {
				if(s_instance == null)
					s_instance = new AtnaConfiguration();
			}
		return s_instance;
	}
	
	/**
     * Read a global property
     */
    private <T> T getOrCreateGlobalProperty(String propertyName, T defaultValue)
    {
		String propertyValue = Context.getAdministrationService().getGlobalProperty(propertyName);
		if(propertyValue != null && !propertyValue.isEmpty())
			return (T)FormatterUtil.fromWireFormat(propertyValue, defaultValue.getClass());
		else
		{
			Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(propertyName, defaultValue.toString()));
			return defaultValue;
		}
    }

    /**
     * Get the SHR root
     * @return
     */
    public String getShrRoot()  { return this.getOrCreateGlobalProperty(PROP_SHR_ROOT, this.m_shrRootDefault); }

    /**
	 * Get internal provider identifiers
	 */
	public String getProviderRoot() {
		return this.getShrRoot() + ".7";
    }

	/**
	 * Get internal location identifier root
	 */
	public String getLocationRoot() {
		return this.getShrRoot() + ".8";
    }

	/**
	 * Get internal patient root identifiers
	 */
	public String getPatientRoot() {
		return this.getShrRoot() + ".9";
    }

    /**
     * Get the ECID root
     * @return
     */
    public String getEcidRoot()  { return this.getOrCreateGlobalProperty(PROP_ECID_ROOT, this.m_shrEcidRootDefault); }
    /**
     * Get the SHR root
     * @return
     */
    public String getEpidRoot()  { return this.getOrCreateGlobalProperty(PROP_EPID_ROOT, this.m_shrEpidRootDefault); }
    
    /**
     * Get the audit repository endpoint
     * @return
     */
    public String getAuditRepositoryEndpoint() { return this.getOrCreateGlobalProperty(PROP_NAME_AR_ENDPOINT, this.m_arEndpointDefault); }
    /**
     * Get the key store file name
     * @return
     */
    public String getKeyStoreFile() { return this.getOrCreateGlobalProperty(PROP_NAME_JKSKEY_STORE, this.m_keyStoreDefault); }
    /**
     * Get the key store password
     * @return
     */
    public String getKeyStorePassword() { return this.getOrCreateGlobalProperty(PROP_NAME_JKSKEY_PASS, this.m_keyStorePasswordDefault); }
    /**
     * Get the trust store file name
     * @return
     */
    public String getTrustStoreFile() { return this.getOrCreateGlobalProperty(PROP_NAME_JKSTRUST_STORE, this.m_trustStoreDefault); }
    /**
     * Get the trust store password
     * @return
     */
    public String getTrustStorePassword() { return this.getOrCreateGlobalProperty(PROP_NAME_JKSTRUST_PASS, this.m_trustStorePasswordDefault); }
    
	/**
	 * Get the configuration of the IHE components from properties
	 * @return
	 */
	public IheConfiguration createShicConfiguration() {
		
		IheConfiguration retVal = new IheConfiguration();
		
		// Security configuration
		JKSStoreInformation trustStoreInfo = new JKSStoreInformation(this.getTrustStoreFile(), this.getTrustStorePassword());
		JKSStoreInformation keyStoreInfo = new JKSStoreInformation(this.getKeyStoreFile(), this.getKeyStorePassword());
		retVal.setTrustStore(trustStoreInfo);
		retVal.setKeyStore(keyStoreInfo);
		
		ImplementationId implementation = Context.getAdministrationService().getImplementationId();
		if(implementation == null)
			retVal.setIdentifier(new DomainIdentifier(this.getShrRoot(), null));
		else
		{
			retVal.setIdentifier(new DomainIdentifier(this.getShrRoot(), implementation.getImplementationId()));
			retVal.setLocalIdentification(new IheIdentification(implementation.getImplementationId(), implementation.getName()));
		}
		
		IheAffinityDomainConfiguration affinityDomain = new IheAffinityDomainConfiguration("DEFAULT");
		IheActorConfiguration auditRepositoryConfiguration = new IheActorConfiguration(IheActorType.AUDIT_REPOSITORY, "AR", false, this.getAuditRepositoryEndpoint(), null, null, null, null);
		affinityDomain.addActor(auditRepositoryConfiguration);
		
		retVal.setAffinityDomain(affinityDomain);
		
		return retVal;
	}

}
