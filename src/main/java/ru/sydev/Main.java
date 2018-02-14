package ru.sydev;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Throwable {
        List<VirtualMachineDescriptor> vms = VirtualMachine.list();

        // loop through the list of local VM descriptors
        // for getting connector address
        for (VirtualMachineDescriptor descriptor : vms) {
            VirtualMachine vm;
            vm = VirtualMachine.attach(descriptor);
            Properties props = vm.getAgentProperties();
            String connectorAddress =
                    props.getProperty("com.sun.management.jmxremote.localConnectorAddress");
            if (connectorAddress == null) {
                continue;
            }

            JMXServiceURL serviceURL = new JMXServiceURL(connectorAddress);
            JMXConnector connector = JMXConnectorFactory.connect(serviceURL);
            MBeanServerConnection mbsc = connector.getMBeanServerConnection();
            Set<ObjectName> objectNames = mbsc.queryNames(null, null);

            for (ObjectName name : objectNames) {
                System.out.println("Object Name=" + name.getCanonicalName());
            }

            connector.close();
        }
    }
}
