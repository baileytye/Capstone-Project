package com.bowtye.decisive.Helpers;

import android.os.AsyncTask;

import com.bowtye.decisive.Models.Requirement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RatingUtils {

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

    public static Float calculateOptionRating(List<Float>requirementRatings, List<Requirement> requirements){
        float rating = 0;
        float weightTotal = 0;

        for(int i = 0; i < requirementRatings.size(); i ++){
            weightTotal += requirements.get(i).getWeight();
            rating += (requirementRatings.get(i) * requirements.get(i).getWeight());
        }

        return rating / weightTotal;
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
