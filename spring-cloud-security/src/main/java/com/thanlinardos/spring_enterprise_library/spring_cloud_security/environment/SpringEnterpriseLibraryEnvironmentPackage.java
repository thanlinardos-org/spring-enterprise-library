package com.thanlinardos.spring_enterprise_library.spring_cloud_security.environment;

/**
 * Marker interface for component scanning of the Spring Enterprise Library Environment package.
 * This includes the certs, datasource and refresh sub-packages, used when refreshing certificates, datasource credentials
 * and other environment properties at runtime.
 * Note that the "refresh" sub-package is required for the "certs" and "datasource" packages to function.
 */
public interface SpringEnterpriseLibraryEnvironmentPackage {
}
