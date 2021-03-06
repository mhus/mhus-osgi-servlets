/**
 * Copyright (C) 2018 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.osgi.jwskarafbridge.impl;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.osgi.api.karaf.AbstractCmd;
import de.mhus.osgi.jwsbridge.JavaWebServiceAdmin;

@Command(scope = "jws", name = "publish", description = "Publish A Web Services")
@Service
public class CmdPublish extends AbstractCmd {

    @Reference(optional = true)
    private JavaWebServiceAdmin admin;

    @Argument(
            index = 0,
            name = "service",
            required = true,
            description = "Service Name",
            multiValued = false)
    String serviceName;

    @Override
    public Object execute2() throws Exception {
        if (admin == null) {
            System.out.println("Admin not set - exiting");
            return null;
        }
        admin.connect(serviceName);
        return null;
    }
}
