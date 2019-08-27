package com.bowtye.decisive.utils;

import android.os.AsyncTask;

import com.bowtye.decisive.models.Option;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.models.Requirement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RatingUtils {

    /**
     * Calculates the rating of a requirement
     * @param expected expected vale
     * @param actual actual value
     * @param moreIsBetter higher number better
     * @return rating of requirement
     */
    public static Float calculateRequirementRating(Double expected, Double actual, Boolean moreIsBetter){
        float rating;

        if(moreIsBetter){
            if(expected == 0){
                return (float) 5;
            }
            rating = (actual.floatValue() / expected.floatValue());
            rating *= 5;
        } else {
            if(actual == 0){
                return (float) 5;
            }
            rating = (expected.floatValue() / actual.floatValue());
            rating *= 5;
        }
        return (rating > 5) ? 5 : rating;
    }

    /**
     * Calculates the ratings of each requirement within an option
     * @param requirements requirements of option
     * @param values actual values of each requirement
     * @return calculated ratings of all requirements
     */
    public static List<Float> calculateAllRequirementRatings(List<Requirement> requirements, List<Double> values){

        List<Float> ratings = new ArrayList<>(Collections.nCopies(requirements.size(), (float) 0));

        for(int i = 0; i < requirements.size(); i ++){
            ratings.set(i, RatingUtils.calculateRequirementRating(
                    requirements.get(i).getExpected(),
                    values.get(i),
                    requirements.get(i).getMoreIsBetter()
            ));
        }
        return ratings;
    }

    /**
     * Calculates the rating of an option given the calculated requirement ratings
     * @param requirementRatings calculated requirement ratings
     * @param requirements requirements of option
     * @return rating of option
     */
    public static Float calculateOptionRating(List<Float>requirementRatings, List<Requirement> requirements){
        float rating = 0;
        float weightTotal = 0;

        for(int i = 0; i < requirementRatings.size(); i ++){
            weightTotal += requirements.get(i).getWeight();
            rating += (requirementRatings.get(i) * requirements.get(i).getWeight());
        }

        return rating / weightTotal;
    }

    public static class CalculateRatingsOfProjectAsyncTask extends AsyncTask<ProjectWithDetails, Void, ProjectWithDetails>{
        ProjectResultAsyncCallback callback;

        public CalculateRatingsOfProjectAsyncTask(ProjectResultAsyncCallback callback){
            this.callback = callback;
        }

        @Override
        protected ProjectWithDetails doInBackground(ProjectWithDetails... projectWithDetails) {
            ProjectWithDetails project = projectWithDetails[0];

            for(Option option : project.getOptionList()){
                option.setRating(
                    RatingUtils.calculateOptionRating(
                      calculateAllRequirementRatings(project.getRequirementList(), option.getRequirementValues()),
                      project.getRequirementList()
                    )
                );
            }
            return project;
        }

        @Override
        protected void onPostExecute(ProjectWithDetails projectWithDetails) {
            callback.updateProjectAfterCalculatingRatings(projectWithDetails);
        }

        public interface ProjectResultAsyncCallback{
            void updateProjectAfterCalculatingRatings(ProjectWithDetails projectWithDetails);
        }
    }

    public static class CalculateRatingOfOptionAsyncTask extends AsyncTask<Option, Void, Option>{

        OptionResultAsyncCallback callback;
        List<Requirement> requirements;

        public CalculateRatingOfOptionAsyncTask(OptionResultAsyncCallback callback, List<Requirement> requirements){
            this.callback = callback;
            this.requirements = requirements;
        }

        @Override
        protected Option doInBackground(Option... options) {
            Option option = options[0];
            option.setRating(
                    RatingUtils.calculateOptionRating(
                            calculateAllRequirementRatings(requirements, option.getRequirementValues()),
                            requirements
                    )
            );
            return option;
        }

        @Override
        protected void onPostExecute(Option option) {
            callback.updateOptionAfterCalculatingRatings(option);
        }

        public interface OptionResultAsyncCallback{
            void updateOptionAfterCalculatingRatings(Option option);
        }
    }

    public static class CalculateRatingsAsyncTask extends AsyncTask<Void, Void, List<Float>> {

        List<Requirement> requirements;
        List<Double> values;
        RatingResultAsyncCallback callback;


        public CalculateRatingsAsyncTask(List<Requirement> requirements, List<Double> values, RatingResultAsyncCallback callback) {
            this.requirements = requirements;
            this.values = values;
            this.callback = callback;
        }

        @Override
        protected List<Float> doInBackground(Void... voids) {
            return RatingUtils.calculateAllRequirementRatings(requirements, values);
        }

        @Override
        protected void onPostExecute(List<Float> floats) {
            callback.updateUIWithRatingResults(floats);
        }
    }

    public interface RatingResultAsyncCallback{
        void updateUIWithRatingResults(List<Float> ratings);
    }

}
