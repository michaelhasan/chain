class ChainentriesController < ApplicationController
  before_filter :get_chain
  # :get_chain is defined at the bottom of the file,
  # and takes the chainid_id given by the routing and
  # converts it to an @chain object.

  # GET /chainentries
  # GET /chainentries.json
  def index
    #@chainentries = Chainentry.all
    @chainentries = @chain.chainentry

    respond_to do |format|
      format.html # index.html.erb
      format.json { render :json => @chainentries }
    end
  end

  # GET /chainentries/1
  # GET /chainentries/1.json
  def show
    @chainentry = @chain.chainentry.find_by_day(params[:id])
    #@chainentry = Chainentry.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render :json => @chainentry }
    end
  end

  # GET /chainentries/new
  # GET /chainentries/new.json
  def new
    @chain = Chain.find(params[:chain_id])
    @chainentry = @chain.chainentry.build
    #@chainentry = Chainentry.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render :json => @chainentry }
    end
  end

  # GET /chainentries/1/edit
  def edit
    @chainentry = @chain.chainentry.find_by_day(params[:id])
    #@chainentry = Chainentry.find(params[:id])
  end

  # POST /chainentries
  # POST /chainentries.json
  def create
    @chainentry = @chain.chainentry.build(params[:chainentry])
    #@chainentry = Chainentry.new(params[:chainentry])

    respond_to do |format|
      if @chainentry.save
        format.html { redirect_to chain_chainentries_url(@chain), :notice => 'Chainentry was successfully created.' }
        format.json { render :json => @chainentry, :status => :created, :location => @chainentry }
      else
        format.html { render :action => "new" }
        format.json { render :json => @chainentry.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /chainentries/1
  # PUT /chainentries/1.json
  def update
    @chainentry = @chain.chainentry.find_by_day(params[:id])
    #@chainentry = Chainentry.find(params[:id])

    respond_to do |format|
      if @chainentry.update_attributes(params[:chainentry])
        format.html { redirect_to chain_chainentries_url(@chain), :notice => 'Chain Entry was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render :action => "edit" }
        format.json { render :json => @chainentry.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /chainentries/1
  # DELETE /chainentries/1.json
  def destroy
    @chainentry = @chain.chainentry.find_by_day(params[:id])
    #@chainentry = @chain.chainentry.find_by_day(params[:day])
    #@chainentry = Chainentry.find(params[:id])
    @chainentry.destroy

    respond_to do |format|
      format.html { redirect_to (chain_chainentries_path(@chain)) }
      format.json { head :no_content }
    end
  end

  private
  # get_chain converts the chain_id given by the routing
  # into an @chain object, for use here and in the view.
  def get_chain
  @chain = Chain.find(params[:chain_id])
end
end
