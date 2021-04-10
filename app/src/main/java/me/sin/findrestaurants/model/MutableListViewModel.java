package me.sin.findrestaurants.model;

import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public abstract class MutableListViewModel<T> extends ListViewModel<T> {

    protected final MutableLiveData<MutationState> mutationState = new MutableLiveData<>();

    public MutableLiveData<MutationState> getMutationState() {
        return mutationState;
    }

    protected abstract boolean insertToDataSource(T data);

    protected abstract boolean deleteFromDataSource(int id);

    public void createData(T data){
        ExecutorService service = ForkJoinPool.commonPool();
        service.submit(() -> {
            try {
                if (insertToDataSource(data)){
                    this.mutationState.postValue(MutationState.SUCCESS);
                }else{
                    this.mutationState.postValue(new MutationState("You can only submit once."));
                }
            }catch (Exception e){
                e.printStackTrace();
                this.mutationState.postValue(new MutationState(e.getMessage()));
            }
        });
    }

    public void deleteData(int id){
        ExecutorService service = ForkJoinPool.commonPool();
        service.submit(() -> {
            try {
               if (deleteFromDataSource(id)){
                   this.mutationState.postValue(MutationState.SUCCESS);
               }else{
                   this.mutationState.postValue(new MutationState("data not exist, try refresh?"));
               }
            }catch (Exception e){
                e.printStackTrace();
                this.mutationState.postValue(new MutationState(e.getMessage()));
            }
        });
    }

    public static class MutationState{

        public final boolean success;
        public String errorMessage;

        public static final MutationState SUCCESS = new MutationState();

        private MutationState() {
            this.success = true;
        }

        public MutationState(String errorMessage) {
            this.success = false;
            this.errorMessage = errorMessage;
        }
    }


}
