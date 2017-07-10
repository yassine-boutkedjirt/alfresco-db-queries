package com.alfresco.support.alfrescodb.controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import com.alfresco.support.alfrescodb.dao.*;
import com.alfresco.support.alfrescodb.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class WebController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${largeFolderSize}")
    private Integer largeFolderSize;

    @Value("${largeTransactionSize}")
    private Integer largeTransactionSize;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    SqlMapperController sqlMapper;

    @RequestMapping("/")
    public String index(String name, Model model) {
        addAdditionalParamsToModel(model);
        return "index";
    }


    @Autowired
    private WorkflowMapper workflowMapper;

    @RequestMapping("/report")
    public void report(Model model) {

        List <RelationInfo> listRelationInfos;
        List <LargeFolder> listLargeFolders;
        List <LargeTransaction> listLargeTransactions;
        List <ActivitiesFeed> listActivitiesFeed;
        List <ArchivedNodes> listArchivedNodes;
        List < NodesList > listNodesByMimeType;
        List < NodesList > listNodesByType;
        List < NodesList > listNodesByStore;
        List < LockedResources > listLockedResources;
        List <Authority> listUsers;
        List <Authority> listGroups;

        List <Workflow> listWorkflows;

      try {
          BufferedWriter out = new BufferedWriter(new FileWriter("report.txt"));

          addAdditionalParamsToModel(model);

          // Database Size
          listRelationInfos = sqlMapper.findTablesInfo();
          out.write("\n\nDatabase Tables Information");
          out.write("\nTable, Total Size, Row Estimate, Table Size, Index Size");
          for(int i=0;i<listRelationInfos.size();i++){
              out.write(listRelationInfos.get(i).printDbInfo());
          }
          model.addAttribute("listRelationInfos", listRelationInfos);

          String dbSize = sqlMapper.findDbSize();
          out.write("\n\nDatabase Size");
          out.write("\nSize");
          out.write(dbSize);
          model.addAttribute("dbSize", dbSize);

          // Large Folders
          listLargeFolders = largeFolderMapper.findBySize(largeFolderSize);
          out.write("\n\nLarge Folders");
          out.write("\nFolder Name, Node Reference, Type, TNo. of Child Nodes");
          for(int i=0;i<listLargeFolders.size();i++){
              out.write(listLargeFolders.get(i).printLargeFolders());
          }
          model.addAttribute("listLargeFolders", listLargeFolders);

          // Large Transactions
          listLargeTransactions = largeTransactionMapper.findBySize(largeTransactionSize);
          out.write("\n\nLarge Transactions");
          out.write("\nTransaction Id, Nodes Count");
          for(int i=0;i<listLargeTransactions.size();i++){
              out.write(listLargeTransactions.get(i).printLargeTransactions());
          }
          model.addAttribute("listLargeTransactions", listLargeTransactions);

          // Activities
          listActivitiesFeed = sqlMapper.findActivitiesByActivityType();
          out.write("\n\nActivities by Activity Type");
          out.write("\nDate, Site Network, Activity Type, Count");
          for(int i=0;i<listActivitiesFeed.size();i++){
              out.write(listActivitiesFeed.get(i).printActivitiesByActivityType());
          }
          model.addAttribute("listActivitiesFeedByActivityType", listActivitiesFeed);

          listActivitiesFeed = sqlMapper.findActivitiesByUser();
          out.write("\n\nActivities by User");
          out.write("\nDate, Site Network, User Id, Count");
          for(int i=0;i<listActivitiesFeed.size();i++){
              out.write(listActivitiesFeed.get(i).printActivitiesByUser());
          }
          model.addAttribute("listActivitiesFeedByUser", listActivitiesFeed);

          listActivitiesFeed = sqlMapper.findActivitiesByApplicationInterface();
          out.write("\n\nActivities by Application Interface");
          out.write("\nDate, Site Network, Application Interface, Count");
          for(int i=0;i<listActivitiesFeed.size();i++){
              out.write(listActivitiesFeed.get(i).printActivitiesByInterface());
          }
          model.addAttribute("listActivitiesFeedByAppTool", listActivitiesFeed);

          /* Workflows */
          listWorkflows = workflowMapper.findAll();
          out.write("All Workflows Grouped by Process Definition and Task Name");
          out.write("\nProcess Definition, Task Name, No Occurrencies");
          for(int i=0;i<listWorkflows.size();i++){
              out.write(listWorkflows.get(i).printTasks());
          }
          model.addAttribute("listWorkflows", listWorkflows);

          List < Workflow > listOpenWorkflows = workflowMapper.openWorkflows();
          out.write("\n\nOpen Workflows");
          out.write("\nProcess Definition, No Occurrencies");
          for(int i=0;i<listWorkflows.size();i++){
              out.write(listWorkflows.get(i).printProcesses());
          }
          model.addAttribute("listOpenWorkflows", listOpenWorkflows);

          List < Workflow > listClosedWorkflows =  workflowMapper.closedWorkflows();
          out.write("\n\nClosed Workflows");
          out.write("\nProcess Definition, No Occurrencies");
          for(int i=0;i<listWorkflows.size();i++){
              out.write(listWorkflows.get(i).printProcesses());
          }
          model.addAttribute("listClosedWorkflows", listClosedWorkflows);

          List < Workflow > listOpenTasks = workflowMapper.openTasks();
          out.write("\n\nOpen Tasks");
          out.write("\nProcess Definition, Task Name, No Occurrencies");
          for(int i=0;i<listOpenTasks.size();i++){
              out.write(listOpenTasks.get(i).printTasks());
          }
          model.addAttribute("listOpenTasks", listOpenTasks);


          List < Workflow > listClosedTasks = workflowMapper.closedTasks();
          out.write("\n\nClosed Tasks");
          out.write("\nProcess Definition, Task Name, No Occurrencies");
          for(int i=0;i<listClosedTasks.size();i++){
              out.write(listClosedTasks.get(i).printTasks());
          }
          model.addAttribute("listClosedTasks", listClosedTasks);

          // Archived Nodes
          listArchivedNodes = archivedNodesMapper.findArchivedNodes();
          out.write("\n\nAll Archived Nodes");
          for(int i=0;i<listArchivedNodes.size();i++){
              out.write(listArchivedNodes.get(i).printArchivedNodes());
          }
          model.addAttribute("listArchivedNodes", listArchivedNodes);

          listArchivedNodes = archivedNodesMapper.findArchivedNodesByUser();
          out.write("\n\nArchived Nodes by User");
          out.write("\nArchived Nodes, User");
          for(int i=0;i<listArchivedNodes.size();i++){
              out.write(listArchivedNodes.get(i).printArchivedNodesByUser());
          }
          model.addAttribute("listArchivedNodesByUser", listArchivedNodes);

          // List Nodes by Mimetype
          listNodesByMimeType = nodeListMapper.findNodesSizeByMimeType();
          out.write("\n\nNodes Disk Space by Mimetype");
          out.write("\nMime Types, Nodes Count, Disk Space");
          for(int i=0;i<listNodesByMimeType.size();i++){
              out.write(listNodesByMimeType.get(i).printNodesByMimeType());
          }
          model.addAttribute("listNodesByMimeType", listNodesByMimeType);

          // Nodes disk space
          List < NodesList > diskSpace = nodeListMapper.findNodesSize();
          model.addAttribute("totalDiskSpace", diskSpace);

          // List Nodes by Content Type
          listNodesByType = sqlMapper.findNodesByContentType();
          out.write("\n\nNodes by Content Type");
          out.write("\nNode Type, Nodes Count");
          for(int i=0;i<listNodesByType.size();i++){
              out.write(listNodesByType.get(i).printNodesByType());
          }
          model.addAttribute("listNodesByType", listNodesByType);

          // List Nodes by Store
          listNodesByStore = sqlMapper.findNodesByStore();
          out.write("\n\nNodes by Store");
          out.write("\nStore, Nodes Count");
          for(int i=0;i<listNodesByStore.size();i++){
              out.write(listNodesByStore.get(i).printNodesByStore());
          }
          model.addAttribute("listNodesByStore", listNodesByStore);

          // Resource Locking
          listLockedResources = lockedResourcesMapper.findAll();
          out.write("\n\nResource Locking");
          out.write("\nIde, Lock Token, Start Time, Expiry Time, Shared Resource, Exclusive Resource, URI");
          for(int i=0;i<listLockedResources.size();i++){
              out.write(listLockedResources.get(i).findAll());
          }
          model.addAttribute("listLockedResources", listLockedResources);

          // Authorities
          out.write("\n\nAuthorities");
          listUsers = authorityMapper.findUsers();
          out.write("\n\nUsers Count");
          for(int i=0;i<listUsers.size();i++){
              out.write(listUsers.get(i).printUsers());
          }
          model.addAttribute("listUsers", listUsers);

          listGroups = authorityMapper.findGroups();
          out.write("\n\nGroups Count");
          for(int i=0;i<listUsers.size();i++){
              out.write(listUsers.get(i).printGroups());
          }
          model.addAttribute("listGroups", listGroups);

          out.close();
        }
        catch (IOException e)
        {
            System.out.println("Exception ");

        }
    }

    @RequestMapping("/workflows")
    public String workflows(Model model) {

    	// Count workflows by process def and task name
        List <Workflow> listWorkflows = workflowMapper.findAll();
        model.addAttribute("listWorkflows", listWorkflows);

    	// Count open processes
        List < Workflow > listOpenWorkflows = workflowMapper.openWorkflows();
        model.addAttribute("listOpenWorkflows", listOpenWorkflows);
        
    	// Count closed processes
        List < Workflow > listClosedWorkflows = workflowMapper.closedWorkflows();
        model.addAttribute("listClosedWorkflows", listClosedWorkflows);
        
    	// Count open taks
        List < Workflow > listOpenTasks = workflowMapper.openTasks();
        model.addAttribute("listOpenTasks", listOpenTasks);
        
    	// Count closed tasks
        List < Workflow > listClosedTasks = workflowMapper.closedTasks();
        model.addAttribute("listClosedTasks", listClosedTasks);
        
        addAdditionalParamsToModel(model);

        return null;
    }

    @Autowired
    private DbSizeMapper dbSizeMapper;

     @RequestMapping("/dbSize")
    public String dbSize(Model model) {
        List <RelationInfo> listRelationInfos = sqlMapper.findTablesInfo();
        String dbSize = sqlMapper.findDbSize();

        model.addAttribute("listRelationInfos", listRelationInfos);
        model.addAttribute("dbSize", dbSize);

        addAdditionalParamsToModel(model);

        return null;
    }

    @Autowired
    private LargeFolderMapper largeFolderMapper;

    @RequestMapping("/largeFolders")
    public String largeFolders(@RequestParam(value = "size", required = true) String size, Model model) {
        List <LargeFolder> listLargeFolders = largeFolderMapper.findBySize(largeFolderSize);

        model.addAttribute("largeFolderSize", largeFolderSize);
        model.addAttribute("listLargeFolders", listLargeFolders);
        model.addAttribute("size", size);
        
        addAdditionalParamsToModel(model);

        return null;
    }

    @Autowired
    private LargeTransactionMapper largeTransactionMapper;

    @RequestMapping("/largeTransactions")
    public String largeTransactions(@RequestParam(value = "size", required = true) String size, Model model) {
        List <LargeTransaction> listLargeTransactions = largeTransactionMapper.findBySize(largeTransactionSize);

        model.addAttribute("largeTransactionSize", largeTransactionSize);
        model.addAttribute("listLargeTransactions", listLargeTransactions);
        model.addAttribute("size", size);

        addAdditionalParamsToModel(model);

        return null;
    }

    @RequestMapping("/activitiesFeed")
    public String activitiesFeed(Model model) {

        // Activities by activity type
        List <ActivitiesFeed> listActivitiesFeed = sqlMapper.findActivitiesByActivityType();

        model.addAttribute("listActivitiesFeedByActivityType", listActivitiesFeed);

        // Activities by user
        listActivitiesFeed = sqlMapper.findActivitiesByUser();

        model.addAttribute("listActivitiesFeedByUser", listActivitiesFeed);

        // Activities by application interface
        listActivitiesFeed = sqlMapper.findActivitiesByApplicationInterface();

        model.addAttribute("listActivitiesFeedByAppTool", listActivitiesFeed);
          
        addAdditionalParamsToModel(model);

        return null;
    }

    @Autowired
    private ArchivedNodesMapper archivedNodesMapper;

    @RequestMapping("/archivedNodes")
    public String archivedNodes(Model model) {

        // Archived nodes
        List <ArchivedNodes> listArchivedNodes = archivedNodesMapper.findArchivedNodes();
        
        model.addAttribute("listArchivedNodes", listArchivedNodes);

        // Archived nodes by user
        List <ArchivedNodes> listArchivedNodesByUser = archivedNodesMapper.findArchivedNodesByUser();

        model.addAttribute("listArchivedNodesByUser", listArchivedNodesByUser);
        addAdditionalParamsToModel(model);

        return null;
    }

    @Autowired
    private NodeListMapper nodeListMapper;

    @RequestMapping("/listNodesByMimeType")
    public void nodesByMimeType(Model model) {

        // Nodes disk space
        List < NodesList > diskSpace = nodeListMapper.findNodesSize();
        
        model.addAttribute("totalDiskSpace", diskSpace);
        
        // Nodes by mime type
        List < NodesList > listNodesByMimeType = nodeListMapper.findNodesSizeByMimeType();

        model.addAttribute("listNodesByMimeType", listNodesByMimeType);

        addAdditionalParamsToModel(model);
    }

    @RequestMapping("/listNodesByType")
    public String nodesByType(Model model) {
        // Nodes by type
        List < NodesList > listNodesByType = sqlMapper.findNodesByContentType();

        model.addAttribute("listNodesByType", listNodesByType);

        addAdditionalParamsToModel(model);

        return null;
    }
    
    @RequestMapping("/listNodesByStore")
    public String nodesByStore(Model model) {
        // Nodes by store
        List < NodesList > listNodesByStore = sqlMapper.findNodesByStore();
        
        model.addAttribute("listNodesByStore", listNodesByStore);

        addAdditionalParamsToModel(model);

        return null;
    }

    @Autowired
    private LockedResourcesMapper lockedResourcesMapper;

    @RequestMapping("/lockedResources")
    public String lockedResources(Model model) {
        			 
        List < LockedResources > listLockedResources = lockedResourcesMapper.findAll();
        
        model.addAttribute("listLockedResources", listLockedResources);
        
        addAdditionalParamsToModel(model);

        return null;
    }

    @Autowired
    private AuthorityMapper authorityMapper;

    @RequestMapping("/authorities")
    public String authorities(Model model) {

        //Count users
       List <Authority> listUsers = authorityMapper.findUsers();

        model.addAttribute("listUsers", listUsers);

        //Count groups
        List < Authority > listGroups = authorityMapper.findGroups();

        model.addAttribute("listGroups", listGroups);

        addAdditionalParamsToModel(model);

        return null;
    }

    private void addAdditionalParamsToModel(Model model) {
        // Need this entry for large folders url
        model.addAttribute("largeFolderSize", largeFolderSize);
        // Need this entry for large transactions url
        model.addAttribute("largeTransactionSize", largeTransactionSize);   		
	}
}
