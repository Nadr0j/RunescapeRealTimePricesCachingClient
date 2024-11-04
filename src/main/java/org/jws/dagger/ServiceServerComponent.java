/* (C)2024 */
package org.jws.dagger;

import dagger.Component;
import org.jws.ServiceServer;

import javax.inject.Singleton;

@Singleton
@Component(modules = {ServiceModules.class})
public interface ServiceServerComponent {
    ServiceServer serviceServer();
}
