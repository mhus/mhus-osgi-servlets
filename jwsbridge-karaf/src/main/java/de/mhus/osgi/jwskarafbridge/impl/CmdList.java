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

import java.io.PrintStream;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.api.karaf.AbstractCmd;
import de.mhus.osgi.jwsbridge.JavaWebServiceAdmin;
import de.mhus.osgi.jwsbridge.WebServiceInfo;

@Command(scope = "jws", name = "list", description = "List current Web Services")
@Service
public class CmdList extends AbstractCmd {

    @Reference(optional = true)
    private JavaWebServiceAdmin admin;

    @Override
    public Object execute2() throws Exception {
        if (admin == null) {
            System.out.println("Admin not set - exiting");
            return null;
        }
        PrintStream out = System.out;
        // session.getConsole();
        ConsoleTable table = new ConsoleTable(tblOpt);
        table.setHeaderValues("Id", "Name", "Bundle", "Status", "Binding");

        for (WebServiceInfo info : admin.getWebServices()) {
            try {
                table.addRowValues(
                        String.valueOf(info.getId()),
                        info.getName(),
                        info.getBundleName(),
                        info.getStatus(),
                        info.getBindingInfo());
            } catch (Throwable t) {
                table.addRowValues(
                        String.valueOf(info.getId()), info.getName(), "", t.toString(), "");
            }
        }
        table.print(out);
        out.flush();
        return null;
    }
}
