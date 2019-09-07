package com.bowtye.decisive.utils;

import com.bowtye.decisive.models.Option;
import com.bowtye.decisive.models.OptionFirebase;
import com.bowtye.decisive.models.Project;
import com.bowtye.decisive.models.ProjectFirebase;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.models.Requirement;
import com.bowtye.decisive.models.RequirementFirebase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectModelConverter {

    /**
     * Converter, drops Room relational ids in process
     *
     * @param projectWithDetails to convert
     * @return converted firebase POJO
     */
    public static ProjectFirebase projectWithDetailsToProjectFirebase(
            ProjectWithDetails projectWithDetails, String userId) {

        if (projectWithDetails != null) {
            ProjectFirebase projectFirebase = new ProjectFirebase();
            projectFirebase.setDateCreated(projectWithDetails.getProject().getDateCreated());
            projectFirebase.setHasPrice(projectWithDetails.getProject().getHasPrice());
            projectFirebase.setName(projectWithDetails.getProject().getName());
            projectFirebase.setOptions(optionToOptionFirebaseList(
                    projectWithDetails.getOptionList(), projectFirebase.getProjectId()));
            projectFirebase.setRequirements(requirementToRequirementFirebaseList(
                    projectWithDetails.getRequirementList(), projectFirebase.getProjectId()));
            projectFirebase.setUserId(userId);
            projectFirebase.setProjectId(projectWithDetails.getProject().getFirebaseId());

            return projectFirebase;
        } else {
            return null;
        }
    }

    /**
     * Converter, Room relational ids set to 0
     *
     * @param projectFirebase to convert
     * @return converted Room POJO
     */
    public static ProjectWithDetails projectFirebaseToProjectWithDetails(ProjectFirebase projectFirebase) {
        if (projectFirebase != null) {
            ProjectWithDetails projectWithDetails = new ProjectWithDetails();
            Project project = new Project(projectFirebase.getName(), projectFirebase.getHasPrice(), projectFirebase.getDateCreated());
            project.setFirebaseId(projectFirebase.getProjectId());

            projectWithDetails.setProject(project);
            projectWithDetails.setOptionList(optionFirebaseToOptionList(projectFirebase.getOptions()));
            projectWithDetails.setRequirementList(requirementFirebaseToRequirementList(projectFirebase.getRequirements()));

            return projectWithDetails;
        } else {
            return null;
        }
    }

    public static List<Option> optionFirebaseToOptionList(List<OptionFirebase> optionFirebaseList) {

        if (optionFirebaseList != null) {
            List<Option> optionList = new ArrayList<>();
            for (OptionFirebase optionFirebase : optionFirebaseList) {
                Option o = new Option(
                        optionFirebase.getName(),
                        optionFirebase.getPrice(),
                        optionFirebase.getRating(),
                        optionFirebase.getRuledOut(),
                        new ArrayList<>(optionFirebase.getRequirementValues()),
                        optionFirebase.getNotes(),
                        optionFirebase.getImagePath());
                optionList.add(o);
            }
            return optionList;
        } else {
            return null;
        }
    }

    public static List<OptionFirebase> optionToOptionFirebaseList(List<Option> optionList, String projectId) {

        if (optionList != null) {
            List<OptionFirebase> optionFirebaseList = new ArrayList<>();

            for (Option option : optionList) {
                optionFirebaseList.add(
                        new OptionFirebase(
                                option.getName(),
                                option.getNotes(),
                                option.getImagePath(),
                                option.getPrice(),
                                new Date(),     //TODO: add date to option model
                                option.getRating(),
                                option.getRuledOut(),
                                new ArrayList<>(option.getRequirementValues())
                        )
                );
            }
            return optionFirebaseList;
        } else {
            return null;
        }
    }

    private static List<Requirement> requirementFirebaseToRequirementList(List<RequirementFirebase> requirementFirebaseList) {

        if (requirementFirebaseList != null) {
            List<Requirement> requirementList = new ArrayList<>();

            for (RequirementFirebase requirementFirebase : requirementFirebaseList) {
                Requirement r = new Requirement(
                        requirementFirebase.getName(),
                        requirementFirebase.getType(),
                        requirementFirebase.getImportance(),
                        requirementFirebase.getExpected(),
                        requirementFirebase.getNotes(),
                        requirementFirebase.getWeight(),
                        requirementFirebase.getMoreIsBetter());
                requirementList.add(r);
            }

            return requirementList;
        } else {
            return null;
        }
    }

    private static List<RequirementFirebase> requirementToRequirementFirebaseList(List<Requirement> requirementList, String projectId) {
        if (requirementList != null) {
            List<RequirementFirebase> requirementFirebaseList = new ArrayList<>();

            for (Requirement requirement : requirementList) {
                requirementFirebaseList.add(
                        new RequirementFirebase(
                                requirement.getName(),
                                requirement.getNotes(),
                                requirement.getType(),
                                requirement.getImportance(),
                                requirement.getExpected(),
                                requirement.getWeight(),
                                requirement.getMoreIsBetter()
                        )
                );
            }

            return requirementFirebaseList;
        } else {
            return null;
        }
    }


}
