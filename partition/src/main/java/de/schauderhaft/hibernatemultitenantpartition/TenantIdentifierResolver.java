/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.schauderhaft.hibernatemultitenantpartition;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

	private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

	public static void setCurrentTenant(String tenant) {
		currentTenant.set(tenant);
	}

	@Override
	public String resolveCurrentTenantIdentifier() {
		final String currentTenant = TenantIdentifierResolver.currentTenant.get();
		return currentTenant == null || currentTenant.isBlank() ? "unknown" : currentTenant;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return false;
	}
}
