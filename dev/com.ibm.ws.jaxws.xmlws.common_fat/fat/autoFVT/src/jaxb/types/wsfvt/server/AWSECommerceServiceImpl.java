//
// @(#) 1.1 autoFVT/src/jaxb/types/wsfvt/server/AWSECommerceServiceImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/29/08 08:46:13 [8/8/12 06:57:15]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 11/14/07 ulbricht    290938          New File
//

package jaxb.types.wsfvt.server;

import java.util.List;
import java.util.ArrayList;

import java.math.BigInteger;
import java.math.BigDecimal;

import javax.xml.ws.Holder;

import javax.jws.WebService;

import jaxb.types.wsfvt.server.amazon.Accessories;
import jaxb.types.wsfvt.server.amazon.Address;
import jaxb.types.wsfvt.server.amazon.Arguments;
import jaxb.types.wsfvt.server.amazon.AWSECommerceService;
import jaxb.types.wsfvt.server.amazon.BrowseNode;
import jaxb.types.wsfvt.server.amazon.BrowseNodeLookup;
import jaxb.types.wsfvt.server.amazon.BrowseNodeLookupRequest;
import jaxb.types.wsfvt.server.amazon.BrowseNodeLookupResponse;
import jaxb.types.wsfvt.server.amazon.BrowseNodes;
import jaxb.types.wsfvt.server.amazon.Cart;
import jaxb.types.wsfvt.server.amazon.CartAdd;
import jaxb.types.wsfvt.server.amazon.CartAddRequest;
import jaxb.types.wsfvt.server.amazon.CartAddResponse;
import jaxb.types.wsfvt.server.amazon.CartClear;
import jaxb.types.wsfvt.server.amazon.CartClearRequest;
import jaxb.types.wsfvt.server.amazon.CartClearResponse;
import jaxb.types.wsfvt.server.amazon.CartCreate;
import jaxb.types.wsfvt.server.amazon.CartCreateRequest;
import jaxb.types.wsfvt.server.amazon.CartCreateResponse;
import jaxb.types.wsfvt.server.amazon.CartGet;
import jaxb.types.wsfvt.server.amazon.CartGetRequest;
import jaxb.types.wsfvt.server.amazon.CartGetResponse;
import jaxb.types.wsfvt.server.amazon.CartItem;
import jaxb.types.wsfvt.server.amazon.CartItems;
import jaxb.types.wsfvt.server.amazon.CartModify;
import jaxb.types.wsfvt.server.amazon.CartModifyRequest;
import jaxb.types.wsfvt.server.amazon.CartModifyResponse;
import jaxb.types.wsfvt.server.amazon.Customer;
import jaxb.types.wsfvt.server.amazon.CustomerContentLookup;
import jaxb.types.wsfvt.server.amazon.CustomerContentLookupRequest;
import jaxb.types.wsfvt.server.amazon.CustomerContentLookupResponse;
import jaxb.types.wsfvt.server.amazon.CustomerContentSearch;
import jaxb.types.wsfvt.server.amazon.CustomerContentSearchRequest;
import jaxb.types.wsfvt.server.amazon.CustomerContentSearchResponse;
import jaxb.types.wsfvt.server.amazon.CustomerReviews;
import jaxb.types.wsfvt.server.amazon.Customers;
import jaxb.types.wsfvt.server.amazon.DecimalWithUnits;
import jaxb.types.wsfvt.server.amazon.EditorialReview;
import jaxb.types.wsfvt.server.amazon.EditorialReviews;
import jaxb.types.wsfvt.server.amazon.Errors;
import jaxb.types.wsfvt.server.amazon.Help;
import jaxb.types.wsfvt.server.amazon.HelpRequest;
import jaxb.types.wsfvt.server.amazon.HelpResponse;
import jaxb.types.wsfvt.server.amazon.HTTPHeaders;
import jaxb.types.wsfvt.server.amazon.Image;
import jaxb.types.wsfvt.server.amazon.Information;
import jaxb.types.wsfvt.server.amazon.Item;
import jaxb.types.wsfvt.server.amazon.ItemAttributes;
import jaxb.types.wsfvt.server.amazon.ItemLookup;
import jaxb.types.wsfvt.server.amazon.ItemLookupRequest;
import jaxb.types.wsfvt.server.amazon.ItemLookupResponse;
import jaxb.types.wsfvt.server.amazon.Items;
import jaxb.types.wsfvt.server.amazon.ItemSearch;
import jaxb.types.wsfvt.server.amazon.ItemSearchRequest;
import jaxb.types.wsfvt.server.amazon.ItemSearchResponse;
import jaxb.types.wsfvt.server.amazon.ListItem;
import jaxb.types.wsfvt.server.amazon.ListLookup;
import jaxb.types.wsfvt.server.amazon.ListLookupRequest;
import jaxb.types.wsfvt.server.amazon.ListLookupResponse;
import jaxb.types.wsfvt.server.amazon.ListmaniaLists;
import jaxb.types.wsfvt.server.amazon.Lists;
import jaxb.types.wsfvt.server.amazon.ListSearch;
import jaxb.types.wsfvt.server.amazon.ListSearchRequest;
import jaxb.types.wsfvt.server.amazon.ListSearchResponse;
import jaxb.types.wsfvt.server.amazon.Merchant;
import jaxb.types.wsfvt.server.amazon.MultiOperation;
import jaxb.types.wsfvt.server.amazon.MultiOperationResponse;
import jaxb.types.wsfvt.server.amazon.NonNegativeIntegerWithUnits;
import jaxb.types.wsfvt.server.amazon.ObjectFactory;
import jaxb.types.wsfvt.server.amazon.Offer;
import jaxb.types.wsfvt.server.amazon.OfferAttributes;
import jaxb.types.wsfvt.server.amazon.OfferListing;
import jaxb.types.wsfvt.server.amazon.Offers;
import jaxb.types.wsfvt.server.amazon.OfferSummary;
import jaxb.types.wsfvt.server.amazon.OperationInformation;
import jaxb.types.wsfvt.server.amazon.OperationRequest;
import jaxb.types.wsfvt.server.amazon.Price;
import jaxb.types.wsfvt.server.amazon.PromotionalTag;
import jaxb.types.wsfvt.server.amazon.Request;
import jaxb.types.wsfvt.server.amazon.ResponseGroupInformation;
import jaxb.types.wsfvt.server.amazon.Review;
import jaxb.types.wsfvt.server.amazon.SavedForLaterItems;
import jaxb.types.wsfvt.server.amazon.SearchInside;
import jaxb.types.wsfvt.server.amazon.SearchResultsMap;
import jaxb.types.wsfvt.server.amazon.Seller;
import jaxb.types.wsfvt.server.amazon.SellerFeedback;
import jaxb.types.wsfvt.server.amazon.SellerListing;
import jaxb.types.wsfvt.server.amazon.SellerListingLookup;
import jaxb.types.wsfvt.server.amazon.SellerListingLookupRequest;
import jaxb.types.wsfvt.server.amazon.SellerListingLookupResponse;
import jaxb.types.wsfvt.server.amazon.SellerListings;
import jaxb.types.wsfvt.server.amazon.SellerListingSearch;
import jaxb.types.wsfvt.server.amazon.SellerListingSearchRequest;
import jaxb.types.wsfvt.server.amazon.SellerListingSearchResponse;
import jaxb.types.wsfvt.server.amazon.SellerLookup;
import jaxb.types.wsfvt.server.amazon.SellerLookupRequest;
import jaxb.types.wsfvt.server.amazon.SellerLookupResponse;
import jaxb.types.wsfvt.server.amazon.Sellers;
import jaxb.types.wsfvt.server.amazon.SimilarityLookup;
import jaxb.types.wsfvt.server.amazon.SimilarityLookupRequest;
import jaxb.types.wsfvt.server.amazon.SimilarityLookupResponse;
import jaxb.types.wsfvt.server.amazon.SimilarProducts;
import jaxb.types.wsfvt.server.amazon.StringWithUnits;
import jaxb.types.wsfvt.server.amazon.Tracks;
import jaxb.types.wsfvt.server.amazon.Transaction;
import jaxb.types.wsfvt.server.amazon.TransactionItem;
import jaxb.types.wsfvt.server.amazon.TransactionLookup;
import jaxb.types.wsfvt.server.amazon.TransactionLookupRequest;
import jaxb.types.wsfvt.server.amazon.TransactionLookupResponse;
import jaxb.types.wsfvt.server.amazon.Transactions;
import jaxb.types.wsfvt.server.amazon.Variations;
import jaxb.types.wsfvt.server.amazon.VariationSummary;
import jaxb.types.wsfvt.server.amazon.AWSECommerceServicePortType;

/**
 * This is a test implementation of the AWSECommerceServicePortType interface.
 */
@WebService(serviceName="AWSECommerceService",
            portName="AWSECommerceServicePort",
            endpointInterface = "jaxb.types.wsfvt.server.amazon.AWSECommerceServicePortType",
            targetNamespace="http://webservices.amazon.com/AWSECommerceService/2005-03-23",
            wsdlLocation="WEB-INF/wsdl/AWSECommerceService.wsdl")
public class AWSECommerceServiceImpl implements AWSECommerceServicePortType {

    /**
     * This is a test implementation of the help method.
     *
     * @param subscriptionId
     * @param associateTag
     * @param validate
     * @param shared
     * @param request
     * @param operationRequest
     * @param information
     */
    public void help(String subscriptionId,
                     String associateTag,
                     String validate,
                     HelpRequest shared,
                     List<HelpRequest> request,
                     Holder<OperationRequest> operationRequest,
                     Holder<List<Information>> information) {

        jaxb.types.wsfvt.server.amazon.ObjectFactory objFactory1 =
            new jaxb.types.wsfvt.server.amazon.ObjectFactory();

        HelpRequest hr = objFactory1.createHelpRequest();
        hr.setAbout("about");
        hr.setHelpType("helpType");
        List<String> l1 = hr.getResponseGroup();
        l1.add("rg1");
        l1.add("rg2");
        List<HelpRequest> hrList = new ArrayList();
        hrList.add(hr);
       
        OperationRequest or = objFactory1.createOperationRequest();
        
        HTTPHeaders httpHeader = objFactory1.createHTTPHeaders();

        List<HTTPHeaders.Header> httpH = httpHeader.getHeader();

        HTTPHeaders.Header h = objFactory1.createHTTPHeadersHeader();
        h.setName("name");
        h.setValue("value");
        httpH.add(h);

        or.setHTTPHeaders(httpHeader);
        or.setRequestId("requestId");

        Arguments arg1 = objFactory1.createArguments();

        List<Arguments.Argument> lArg = arg1.getArgument();

        Arguments.Argument arg2 = objFactory1.createArgumentsArgument();
        arg2.setName("name");
        arg2.setValue("value");
        
        lArg.add(arg2);

        or.setArguments(arg1);
        
        Errors errors = objFactory1.createErrors();
        List<Errors.Error> lErrors = errors.getError();

        Errors.Error e = objFactory1.createErrorsError();
        e.setCode("code");
        e.setMessage("message");

        lErrors.add(e);

        or.setErrors(errors);
        
        or.setRequestProcessingTime(1.2323289F);

        operationRequest.value = or;

        Information info = objFactory1.createInformation();

        Request r = objFactory1.createRequest();
        info.setRequest(r);

        r.setIsValid("true");
        r.setHelpRequest(hr);

        BrowseNodeLookupRequest bnlr = objFactory1.createBrowseNodeLookupRequest();
        List<String> bnlrl1 = bnlr.getBrowseNodeId();
        bnlrl1.add("browseNodeId");

        List<String> bnlrl2 = bnlr.getResponseGroup();
        bnlrl2.add("responseGroup");

        r.setBrowseNodeLookupRequest(bnlr);

        ItemSearchRequest isr = objFactory1.createItemSearchRequest();
        isr.setManufacturer("manufacturer");
        isr.setMaximumPrice(new BigInteger("101"));
        isr.setMerchantId("merchantId");
        isr.setMinimumPrice(new BigInteger("102"));
        isr.setMusicLabel("musicLabel");
        isr.setNeighborhood("neighborhood");
        isr.setOrchestra("orchestra");
        isr.setPostalCode("postalCode");
        isr.setPower("power");
        isr.setPublisher("publisher");
        List<String> isrRg = isr.getResponseGroup();
        isrRg.add("rg123");
        isr.setSearchIndex("searchIndex");
        isr.setSort("sort");
        isr.setState("state");
        isr.setTextStream("textStream");
        isr.setTitle("title");
        r.setItemSearchRequest(isr);

        ItemLookupRequest ilr = objFactory1.createItemLookupRequest();
        ilr.setCondition("condition");
        ilr.setDeliveryMethod("deliveryDate");
        ilr.setFutureLaunchDate("futureLaunchDate");
        ilr.setIdType("idType");
        ilr.setISPUPostalCode("ispuPostalCode");
        ilr.setMerchantId("merchantId");
        ilr.setOfferPage(new BigInteger("103"));
        List<String> ilrl1 = ilr.getItemId();
        ilrl1.add("itemId");
        List<String> ilrRg = ilr.getResponseGroup();
        isrRg.add("rg1234");
        ilr.setReviewPage(new BigInteger("104"));
        ilr.setSearchIndex("searchIndex");
        ilr.setSearchInsideKeywords("searchInsideKeywords");
        ilr.setVariationPage(new BigInteger("105"));
        r.setItemLookupRequest(ilr);

        ListSearchRequest lsr = objFactory1.createListSearchRequest();
        lsr.setCity("Austin");
        lsr.setEmail("webservices@us.ibm.com");
        lsr.setFirstName("Web");
        lsr.setLastName("Services");
        lsr.setListPage(new BigInteger("106"));
        lsr.setListType("listType");
        lsr.setName("name");
        List<String> lsrRg = lsr.getResponseGroup();
        lsrRg.add("rg12345");
        lsr.setState("TX");
        r.setListSearchRequest(lsr);
        
        ListLookupRequest llr = objFactory1.createListLookupRequest();
        llr.setCondition("condition");
        llr.setDeliveryMethod("deliveryMethod");
        llr.setISPUPostalCode("postalCode");
        llr.setListId("listId");
        llr.setListType("listType");
        llr.setMerchantId("merchantId");
        llr.setProductGroup("productGroup");
        llr.setProductPage(new BigInteger("107"));
        List<String> llrRg = llr.getResponseGroup();
        llrRg.add("rg123456");
        llr.setSort("sort");
        r.setListLookupRequest(llr);

        CustomerContentSearchRequest ccsr = objFactory1.createCustomerContentSearchRequest();
        ccsr.setCustomerPage(new BigInteger("108"));
        ccsr.setEmail("email");
        ccsr.setName("name");
        List<String> ccsrRg = ccsr.getResponseGroup();
        ccsrRg.add("rg1234567");
        r.setCustomerContentSearchRequest(ccsr);
        
        CustomerContentLookupRequest cclr = objFactory1.createCustomerContentLookupRequest();
        cclr.setCustomerId("customerId");
        List<String> cclrRg = cclr.getResponseGroup();
        cclrRg.add("rg12345678");
        cclr.setReviewPage(new BigInteger("109"));
        r.setCustomerContentLookupRequest(cclr);

        SimilarityLookupRequest slr = objFactory1.createSimilarityLookupRequest();
        slr.setCondition("condition");
        slr.setDeliveryMethod("deliveryMethod");
        List<String> slrId = slr.getItemId();
        slr.setISPUPostalCode("postalCode");
        slr.setMerchantId("merchantId");
        List<String> slrRg = slr.getResponseGroup();
        slrRg.add("rg123456789");
        slr.setSimilarityType("similarityType");
        r.setSimilarityLookupRequest(slr);

        CartGetRequest cgr = objFactory1.createCartGetRequest();
        cgr.setCartId("cartId");
        cgr.setHMAC("HMAC");
        cgr.setMergeCart("mergeCart");
        List<String> cgrRg = cgr.getResponseGroup();
        cgrRg.add("rg12345678910");
        r.setCartGetRequest(cgr);

        CartAddRequest car = objFactory1.createCartAddRequest();
        car.setCartId("cartId");
        car.setHMAC("HMAC");
        car.setMergeCart("mergeCart");
        List<String> carRg = car.getResponseGroup();
        carRg.add("rg12345678910");
        r.setCartAddRequest(car);

        CartAddRequest.Items carItem = objFactory1.createCartAddRequestItems();

        List<CartAddRequest.Items.Item> carLItem = carItem.getItem();

        CartAddRequest.Items.Item carItems = objFactory1.createCartAddRequestItemsItem();

        carItems.setASIN("asin");
        carItems.setOfferListingId("offerListingId");
        carItems.setQuantity(new BigInteger("110"));
        carItems.setAssociateTag("associateTag");
        carItems.setListItemId("listItemId");
        carLItem.add(carItems);

        car.setItems(carItem);

        CartCreateRequest ccr = objFactory1.createCartCreateRequest();
        ccr.setMergeCart("setMergeCart");
        List<String> ccrRg = ccr.getResponseGroup();
        ccrRg.add("listing1");
        ccrRg.add("listing2");
        r.setCartCreateRequest(ccr);

        CartCreateRequest.Items ccrItem = objFactory1.createCartCreateRequestItems();
        
        List<CartCreateRequest.Items.Item> ccrLItem = ccrItem.getItem();

        CartCreateRequest.Items.Item ccrItems = objFactory1.createCartCreateRequestItemsItem();

        ccrItems.setASIN("ASIN");
        ccrItems.setOfferListingId("OfferListingId");
        ccrItems.setQuantity(new BigInteger("2323"));
        ccrItems.setAssociateTag("AssociateTag");
        ccrItems.setListItemId("ListItemId");
        
        ccrLItem.add(ccrItems);
        
        ccr.setItems(ccrItem);

        CartModifyRequest cmr = objFactory1.createCartModifyRequest();
        cmr.setCartId("CartId");
        cmr.setHMAC("HMAC");
        cmr.setMergeCart("MergeCart");
        
        CartModifyRequest.Items cmrItem = objFactory1.createCartModifyRequestItems();
        
        List<CartModifyRequest.Items.Item> cmrLItem = cmrItem.getItem();

        CartModifyRequest.Items.Item cmrItems = objFactory1.createCartModifyRequestItemsItem();

        cmrItems.setAction("action");
        cmrItems.setCartItemId("CartItemId");
        cmrItems.setQuantity(new BigInteger("2342"));
        
        cmrLItem.add(cmrItems);

        cmr.setItems(cmrItem);

        List<String> cmrRg = cmr.getResponseGroup();
        cmrRg.add("listing1");
        cmrRg.add("listing2");
        r.setCartModifyRequest(cmr);

        CartClearRequest ccr2 = objFactory1.createCartClearRequest();
        ccr2.setCartId("CartId");
        ccr2.setHMAC("HMAC");
        ccr2.setMergeCart("MergeCart");
        List<String> ccrrRg = ccr2.getResponseGroup();
        ccrrRg.add("listing1");
        ccrrRg.add("listing2");
        r.setCartClearRequest(ccr2);

        TransactionLookupRequest tlr = objFactory1.createTransactionLookupRequest();
        List<String> tlrRg = tlr.getResponseGroup();
        tlrRg.add("listing1");
        tlrRg.add("listing2");
        List<String> tlrTId = tlr.getTransactionId();
        tlrTId.add("transactionID1");
        tlrTId.add("transactionID2");
        r.setTransactionLookupRequest(tlr);

        SellerListingSearchRequest slsr = objFactory1.createSellerListingSearchRequest();
        slsr.setBrowseNode("BrowseNode");
        slsr.setCountry("Country");
        slsr.setKeywords("Keywords");
        slsr.setListingPage(new BigInteger("121"));
        slsr.setOfferStatus("OfferStatus");
        slsr.setPostalCode("PostalCode");
        List<String> slsrRg = slsr.getResponseGroup();
        slsrRg.add("listing1");
        slsrRg.add("listing2");
        slsr.setSearchIndex("SearchIndex");
        slsr.setSellerId("SellerId");
        slsr.setShipOption("ShipOption");
        slsr.setSort("Sort");
        slsr.setTitle("Title");
        r.setSellerListingSearchRequest(slsr);

        SellerListingLookupRequest sllr = objFactory1.createSellerListingLookupRequest();
        sllr.setId("id");
        sllr.setIdType("IdType");
        List<String> sllrRg = sllr.getResponseGroup();
        sllrRg.add("listing1");
        sllrRg.add("listing2");
        r.setSellerListingLookupRequest(sllr);

        SellerLookupRequest slr2 = objFactory1.createSellerLookupRequest();
        List<String> slrRg2 = slr2.getResponseGroup();
        slrRg2.add("listing1");
        slrRg2.add("listing2");
        List<String> slr2Id = slr2.getSellerId();
        slr2Id.add("id1");
        slr2Id.add("id2");
        slr2.setFeedbackPage(new BigInteger("1223"));
        r.setSellerLookupRequest(slr2);

        Errors errors2 = objFactory1.createErrors();
        List<Errors.Error> lErrors2 = errors.getError();

        Errors.Error e2 = objFactory1.createErrorsError();
        e2.setCode("code");
        e2.setMessage("message");

        lErrors2.add(e2);
        r.setErrors(errors2);

        List<Information> listInfo = new ArrayList();
        listInfo.add(info);

        information.value = listInfo;

    }

    public void itemSearch(String subscriptionId,
                           String associateTag,
                           String xmlEscaping,
                           String validate,
                           ItemSearchRequest shared,
                           List<ItemSearchRequest> request,
                           Holder<OperationRequest> operationRequest,
                           Holder<List<Items>> items) {

    }

    public void itemLookup(String subscriptionId,
                           String associateTag,
                           String validate,
                           String xmlEscaping,
                           ItemLookupRequest shared,
                           List<ItemLookupRequest> request,
                           Holder<OperationRequest> operationRequest,
                           Holder<List<Items>> items) {


    }

    public void browseNodeLookup(String subscriptionId,
                                 String associateTag,
                                 String validate,
                                 String xmlEscaping,
                                 BrowseNodeLookupRequest shared,
                                 List<BrowseNodeLookupRequest> request,
                                 Holder<OperationRequest> operationRequest,
                                 Holder<List<BrowseNodes>> browseNodes) {

    }



    public void listSearch(String subscriptionId,
                           String associateTag,
                           String validate,
                           String xmlEscaping,
                           ListSearchRequest shared,
                           List<ListSearchRequest> request,
                           Holder<OperationRequest> operationRequest,
                           Holder<List<Lists>> lists) {

    }

    public void listLookup(String subscriptionId,
                           String associateTag,
                           String validate,
                           String xmlEscaping,
                           ListLookupRequest shared,
                           List<ListLookupRequest> request,
                           Holder<OperationRequest> operationRequest,
                           Holder<List<Lists>> lists) {

    }


    public void customerContentSearch(String subscriptionId,
                                      String associateTag,
                                      String validate,
                                      String xmlEscaping,
                                      CustomerContentSearchRequest shared,
                                      List<CustomerContentSearchRequest> request,
                                      Holder<OperationRequest> operationRequest,
                                      Holder<List<Customers>> customers) {

    }

    public void customerContentLookup(String subscriptionId,
                                      String associateTag,
                                      String validate,
                                      String xmlEscaping,
                                      CustomerContentLookupRequest shared,
                                      List<CustomerContentLookupRequest> request,
                                      Holder<OperationRequest> operationRequest,
                                      Holder<List<Customers>> customers) {

    }


    public void similarityLookup(String subscriptionId,
                                 String associateTag,
                                 String validate,
                                 String xmlEscaping,
                                 SimilarityLookupRequest shared,
                                 List<SimilarityLookupRequest> request,
                                 Holder<OperationRequest> operationRequest,
                                 Holder<List<Items>> items) {
    }

    public void sellerLookup(String subscriptionId,
                             String associateTag,
                             String validate,
                             String xmlEscaping,
                             SellerLookupRequest shared,
                             List<SellerLookupRequest> request,
                             Holder<OperationRequest> operationRequest,
                             Holder<List<Sellers>> sellers) {
    }



    public void cartGet(String subscriptionId,
                        String associateTag,
                        String validate,
                        String xmlEscaping,
                        CartGetRequest shared,
                        List<CartGetRequest> request,
                        Holder<OperationRequest> operationRequest,
                        Holder<List<Cart>> cart) {
    }



    public void cartCreate(String subscriptionId,
                           String associateTag,
                           String validate,
                           String xmlEscaping,
                           CartCreateRequest shared,
                           List<CartCreateRequest> request,
                           Holder<OperationRequest> operationRequest,
                           Holder<List<Cart>> cart) {

    }

    public void cartAdd(String subscriptionId,
                        String associateTag,
                        String validate,
                        String xmlEscaping,
                        CartAddRequest shared,
                        List<CartAddRequest> request,
                        Holder<OperationRequest> operationRequest,
                        Holder<List<Cart>> cart) {

    }

    public void cartModify(String subscriptionId,
                           String associateTag,
                           String validate,
                           String xmlEscaping,
                           CartModifyRequest shared,
                           List<CartModifyRequest> request,
                           Holder<OperationRequest> operationRequest,
                           Holder<List<Cart>> cart) {
    }

    public void cartClear(String subscriptionId,
                          String associateTag,
                          String validate,
                          String xmlEscaping,
                          CartClearRequest shared,
                          List<CartClearRequest> request,
                          Holder<OperationRequest> operationRequest,
                          Holder<List<Cart>> cart) {

    }

    public void transactionLookup(String subscriptionId,
                                  String associateTag,
                                  String validate,
                                  String xmlEscaping,
                                  TransactionLookupRequest shared,
                                  List<TransactionLookupRequest> request,
                                  Holder<OperationRequest> operationRequest,
                                  Holder<List<Transactions>> transactions) {

    }


    public void sellerListingSearch(String subscriptionId,
                                    String associateTag,
                                    String validate,
                                    String xmlEscaping,
                                    SellerListingSearchRequest shared,
                                    List<SellerListingSearchRequest> request,
                                    Holder<OperationRequest> operationRequest,
                                    Holder<List<SellerListings>> sellerListings) {

    }




    public void sellerListingLookup(String subscriptionId,
                                    String associateTag,
                                    String validate,
                                    String xmlEscaping,
                                    SellerListingLookupRequest shared,
                                    List<SellerListingLookupRequest> request,
                                    Holder<OperationRequest> operationRequest,
                                    Holder<List<SellerListings>> sellerListings) {

    }

    /**
     * This is a test implementation for the multiOperation method
     * from the Amazon WSDL.
     *
     * @param help
     * @param itemSearch
     * @param itemLookup
     * @param listSearch
     * @param listLookup
     * @param customerContentSearch
     * @param customerContentLookup
     * @param similarityLookup
     * @param sellerLookup
     * @param cartGet
     * @param cartAdd
     * @param cartCreate
     * @param cartModify
     * @param cartClear
     * @param transactionLookup
     * @param sellerListingSearch
     * @param sellerListingLookup
     * @param browseNodeLookup
     * @param operationRequest
     * @param helpResponse
     * @param itemSearchResponse
     * @param itemLookupResponse
     * @param listSearchResponse
     * @param listLookupResponse
     * @param customerContentSearchResponse
     * @param customerContentLookupResponse
     * @param similarityLookupResponse
     * @param sellerLookupResponse
     * @param cartGetResponse
     * @param cartAddResponse
     * @param cartCreateResponse
     * @param cartModifyResponse
     * @param cartClearResponse
     * @param transactionLookupResponse
     * @param sellerListingSearchResponse
     * @param sellerListingLookupResponse
     * @param browseNodeLookupResponse
     */
    public void multiOperation(Help help,
                               ItemSearch itemSearch,
                               ItemLookup itemLookup,
                               ListSearch listSearch,
                               ListLookup listLookup,
                               CustomerContentSearch customerContentSearch,
                               CustomerContentLookup customerContentLookup,
                               SimilarityLookup similarityLookup,
                               SellerLookup sellerLookup,
                               CartGet cartGet,
                               CartAdd cartAdd,
                               CartCreate cartCreate,
                               CartModify cartModify,
                               CartClear cartClear,
                               TransactionLookup transactionLookup,
                               SellerListingSearch sellerListingSearch,
                               SellerListingLookup sellerListingLookup,
                               BrowseNodeLookup browseNodeLookup,
                               Holder<OperationRequest> operationRequest,
                               Holder<HelpResponse> helpResponse,
                               Holder<ItemSearchResponse> itemSearchResponse,
                               Holder<ItemLookupResponse> itemLookupResponse,
                               Holder<ListSearchResponse> listSearchResponse,
                               Holder<ListLookupResponse> listLookupResponse,
                               Holder<CustomerContentSearchResponse> customerContentSearchResponse,
                               Holder<CustomerContentLookupResponse> customerContentLookupResponse,
                               Holder<SimilarityLookupResponse> similarityLookupResponse,
                               Holder<SellerLookupResponse> sellerLookupResponse,
                               Holder<CartGetResponse> cartGetResponse,
                               Holder<CartAddResponse> cartAddResponse,
                               Holder<CartCreateResponse> cartCreateResponse,
                               Holder<CartModifyResponse> cartModifyResponse,
                               Holder<CartClearResponse> cartClearResponse,
                               Holder<TransactionLookupResponse> transactionLookupResponse,
                               Holder<SellerListingSearchResponse> sellerListingSearchResponse,
                               Holder<SellerListingLookupResponse> sellerListingLookupResponse,
                               Holder<BrowseNodeLookupResponse> browseNodeLookupResponse) {

        jaxb.types.wsfvt.server.amazon.ObjectFactory objFactory1 =
            new jaxb.types.wsfvt.server.amazon.ObjectFactory();

        OperationRequest or = objFactory1.createOperationRequest();
        
        HTTPHeaders httpHeader = objFactory1.createHTTPHeaders();

        List<HTTPHeaders.Header> httpH = httpHeader.getHeader();

        HTTPHeaders.Header h = objFactory1.createHTTPHeadersHeader();
        h.setName("name");
        h.setValue("value");
        httpH.add(h);

        or.setHTTPHeaders(httpHeader);
        or.setRequestId("requestId");

        Arguments arg1 = objFactory1.createArguments();

        List<Arguments.Argument> lArg = arg1.getArgument();

        Arguments.Argument arg2 = objFactory1.createArgumentsArgument();
        arg2.setName("name");
        arg2.setValue("value");
        
        lArg.add(arg2);

        or.setArguments(arg1);
        
        Errors errors = objFactory1.createErrors();
        List<Errors.Error> lErrors = errors.getError();

        Errors.Error e = objFactory1.createErrorsError();
        e.setCode("code");
        e.setMessage("message");

        lErrors.add(e);

        or.setErrors(errors);
        
        or.setRequestProcessingTime(1.23232F);

        operationRequest.value = or;

        /*
        HelpResponse hResponse = objFactory1.createHelpResponse();
        hResponse.setOperationRequest(or);
        List<Information> lInformation = hResponse.getInformation();
        
        Information info = objFactory1.createInformation();

        Request r = objFactory1.createRequest();
        info.setRequest(r);

        r.setIsValid("true");
        r.setHelpRequest(hr);

        BrowseNodeLookupRequest bnlr = objFactory1.createBrowseNodeLookupRequest();
        List<String> bnlrl1 = bnlr.getBrowseNodeId();
        bnlrl1.add("browseNodeId");

        List<String> bnlrl2 = bnlr.getResponseGroup();
        bnlrl2.add("responseGroup");

        r.setBrowseNodeLookupRequest(bnlr);

        ItemSearchRequest isr = objFactory1.createItemSearchRequest();
        isr.setManufacturer("manufacturer");
        isr.setMaximumPrice(new BigInteger("101"));
        isr.setMerchantId("merchantId");
        isr.setMinimumPrice(new BigInteger("102"));
        isr.setMusicLabel("musicLabel");
        isr.setNeighborhood("neighborhood");
        isr.setOrchestra("orchestra");
        isr.setPostalCode("postalCode");
        isr.setPower("power");
        isr.setPublisher("publisher");
        List<String> isrRg = isr.getResponseGroup();
        isrRg.add("rg123");
        isr.setSearchIndex("searchIndex");
        isr.setSort("sort");
        isr.setState("state");
        isr.setTextStream("textStream");
        isr.setTitle("title");
        r.setItemSearchRequest(isr);

        ItemLookupRequest ilr = objFactory1.createItemLookupRequest();
        ilr.setCondition("condition");
        ilr.setDeliveryMethod("deliveryDate");
        ilr.setFutureLaunchDate("futureLaunchDate");
        ilr.setIdType("idType");
        ilr.setISPUPostalCode("ispuPostalCode");
        ilr.setMerchantId("merchantId");
        ilr.setOfferPage(new BigInteger("103"));
        List<String> ilrl1 = ilr.getItemId();
        ilrl1.add("itemId");
        List<String> ilrRg = ilr.getResponseGroup();
        isrRg.add("rg1234");
        ilr.setReviewPage(new BigInteger("104"));
        ilr.setSearchIndex("searchIndex");
        ilr.setSearchInsideKeywords("searchInsideKeywords");
        ilr.setVariationPage(new BigInteger("105"));
        r.setItemLookupRequest(ilr);

        ListSearchRequest lsr = objFactory1.createListSearchRequest();
        lsr.setCity("Austin");
        lsr.setEmail("webservices@us.ibm.com");
        lsr.setFirstName("Web");
        lsr.setLastName("Services");
        lsr.setListPage(new BigInteger("106"));
        lsr.setListType("listType");
        lsr.setName("name");
        List<String> lsrRg = lsr.getResponseGroup();
        lsrRg.add("rg12345");
        lsr.setState("TX");
        r.setListSearchRequest(lsr);
        
        ListLookupRequest llr = objFactory1.createListLookupRequest();
        llr.setCondition("condition");
        llr.setDeliveryMethod("deliveryMethod");
        llr.setISPUPostalCode("postalCode");
        llr.setListId("listId");
        llr.setListType("listType");
        llr.setMerchantId("merchantId");
        llr.setProductGroup("productGroup");
        llr.setProductPage(new BigInteger("107"));
        List<String> llrRg = llr.getResponseGroup();
        llrRg.add("rg123456");
        llr.setSort("sort");
        r.setListLookupRequest(llr);

        CustomerContentSearchRequest ccsr = objFactory1.createCustomerContentSearchRequest();
        ccsr.setCustomerPage(new BigInteger("108"));
        ccsr.setEmail("email");
        ccsr.setName("name");
        List<String> ccsrRg = ccsr.getResponseGroup();
        ccsrRg.add("rg1234567");
        r.setCustomerContentSearchRequest(ccsr);
        
        CustomerContentLookupRequest cclr = objFactory1.createCustomerContentLookupRequest();
        cclr.setCustomerId("customerId");
        List<String> cclrRg = cclr.getResponseGroup();
        cclrRg.add("rg12345678");
        cclr.setReviewPage(new BigInteger("109"));
        r.setCustomerContentLookupRequest(cclr);

        SimilarityLookupRequest slr = objFactory1.createSimilarityLookupRequest();
        slr.setCondition("condition");
        slr.setDeliveryMethod("deliveryMethod");
        List<String> slrId = slr.getItemId();
        slr.setISPUPostalCode("postalCode");
        slr.setMerchantId("merchantId");
        List<String> slrRg = slr.getResponseGroup();
        slrRg.add("rg123456789");
        slr.setSimilarityType("similarityType");
        r.setSimilarityLookupRequest(slr);

        CartGetRequest cgr = objFactory1.createCartGetRequest();
        cgr.setCartId("cartId");
        cgr.setHMAC("HMAC");
        cgr.setMergeCart("mergeCart");
        List<String> cgrRg = cgr.getResponseGroup();
        cgrRg.add("rg12345678910");
        r.setCartGetRequest(cgr);

        CartAddRequest car = objFactory1.createCartAddRequest();
        car.setCartId("cartId");
        car.setHMAC("HMAC");
        car.setMergeCart("mergeCart");
        List<String> carRg = car.getResponseGroup();
        carRg.add("rg12345678910");
        r.setCartAddRequest(car);

        CartAddRequest.Items carItem = objFactory1.createCartAddRequestItems();

        List<CartAddRequest.Items.Item> carLItem = carItem.getItem();

        CartAddRequest.Items.Item carItems = objFactory1.createCartAddRequestItemsItem();

        carItems.setASIN("asin");
        carItems.setOfferListingId("offerListingId");
        carItems.setQuantity(new BigInteger("110"));
        carItems.setAssociateTag("associateTag");
        carItems.setListItemId("listItemId");
        carLItem.add(carItems);

        car.setItems(carItem);

        CartCreateRequest ccr = objFactory1.createCartCreateRequest();
        ccr.setMergeCart("setMergeCart");
        List<String> ccrRg = ccr.getResponseGroup();
        ccrRg.add("listing1");
        ccrRg.add("listing2");
        r.setCartCreateRequest(ccr);

        CartCreateRequest.Items ccrItem = objFactory1.createCartCreateRequestItems();
        
        List<CartCreateRequest.Items.Item> ccrLItem = ccrItem.getItem();

        CartCreateRequest.Items.Item ccrItems = objFactory1.createCartCreateRequestItemsItem();

        ccrItems.setASIN("ASIN");
        ccrItems.setOfferListingId("OfferListingId");
        ccrItems.setQuantity(new BigInteger("2323"));
        ccrItems.setAssociateTag("AssociateTag");
        ccrItems.setListItemId("ListItemId");
        
        ccrLItem.add(ccrItems);
        
        ccr.setItems(ccrItem);

        CartModifyRequest cmr = objFactory1.createCartModifyRequest();
        cmr.setCartId("CartId");
        cmr.setHMAC("HMAC");
        cmr.setMergeCart("MergeCart");
        
        CartModifyRequest.Items cmrItem = objFactory1.createCartModifyRequestItems();
        
        List<CartModifyRequest.Items.Item> cmrLItem = cmrItem.getItem();

        CartModifyRequest.Items.Item cmrItems = objFactory1.createCartModifyRequestItemsItem();

        cmrItems.setAction("action");
        cmrItems.setCartItemId("CartItemId");
        cmrItems.setQuantity(new BigInteger("2342"));
        
        cmrLItem.add(cmrItems);

        cmr.setItems(cmrItem);

        List<String> cmrRg = cmr.getResponseGroup();
        cmrRg.add("listing1");
        cmrRg.add("listing2");
        r.setCartModifyRequest(cmr);

        CartClearRequest ccr2 = objFactory1.createCartClearRequest();
        ccr2.setCartId("CartId");
        ccr2.setHMAC("HMAC");
        ccr2.setMergeCart("MergeCart");
        List<String> ccrrRg = ccr2.getResponseGroup();
        ccrrRg.add("listing1");
        ccrrRg.add("listing2");
        r.setCartClearRequest(ccr2);

        TransactionLookupRequest tlr = objFactory1.createTransactionLookupRequest();
        List<String> tlrRg = tlr.getResponseGroup();
        tlrRg.add("listing1");
        tlrRg.add("listing2");
        List<String> tlrTId = tlr.getTransactionId();
        tlrTId.add("transactionID1");
        tlrTId.add("transactionID2");
        r.setTransactionLookupRequest(tlr);

        SellerListingSearchRequest slsr = objFactory1.createSellerListingSearchRequest();
        slsr.setBrowseNode("BrowseNode");
        slsr.setCountry("Country");
        slsr.setKeywords("Keywords");
        slsr.setListingPage(new BigInteger("121"));
        slsr.setOfferStatus("OfferStatus");
        slsr.setPostalCode("PostalCode");
        List<String> slsrRg = slsr.getResponseGroup();
        slsrRg.add("listing1");
        slsrRg.add("listing2");
        slsr.setSearchIndex("SearchIndex");
        slsr.setSellerId("SellerId");
        slsr.setShipOption("ShipOption");
        slsr.setSort("Sort");
        slsr.setTitle("Title");
        r.setSellerListingSearchRequest(slsr);

        SellerListingLookupRequest sllr = objFactory1.createSellerListingLookupRequest();
        sllr.setId("id");
        sllr.setIdType("IdType");
        List<String> sllrRg = sllr.getResponseGroup();
        sllrRg.add("listing1");
        sllrRg.add("listing2");
        r.setSellerListingLookupRequest(sllr);

        SellerLookupRequest slr2 = objFactory1.createSellerLookupRequest();
        List<String> slrRg2 = slr2.getResponseGroup();
        slrRg2.add("listing1");
        slrRg2.add("listing2");
        List<String> slr2Id = slr2.getSellerId();
        slr2Id.add("id1");
        slr2Id.add("id2");
        slr2.setFeedbackPage(new BigInteger("1223"));
        r.setSellerLookupRequest(slr2);

        Errors errors2 = objFactory1.createErrors();
        List<Errors.Error> lErrors2 = errors.getError();

        Errors.Error e2 = objFactory1.createErrorsError();
        e2.setCode("code");
        e2.setMessage("message");

        lErrors2.add(e2);
        r.setErrors(errors2);

        List<Information> listInfo = new ArrayList();
        listInfo.add(info);

        lInformation.add(info);

        helpResponse.value = hResponse;

        ItemSearchResponse isr = objFactory1.createItemSearchResponse();
        isr.setOperationRequest(or);
        List<Items> items = isr.getItems();

        Items itms = objFactory1.createItems();
        items.add(itms);

        itms.setTotalResults(new BigInteger("1"));
        itms.setTotalPages(new BigInteger("2"));

        Request request = objectFactory1.createRequest();
        request.setIsValid("isValid");
        
        HelpRequest hr = objFactory1.createHelpRequest();
        hr.setAbout("about");
        hr.setHelpType("helpType");
        List<String> l1 = hr.getResponseGroup();
        l1.add("rg1");
        l1.add("rg2");
        
        request.setHelpRequest(hr);

        BrowseNodeLookupRequest bnlr = objFactory1.createBrowseNodeLookupRequest();
        List<String> bnlrl1 = bnlr.getBrowseNodeId();
        bnlrl1.add("browseNodeId");

        List<String> bnlrl2 = bnlr.getResponseGroup();
        bnlrl2.add("responseGroup");
        
        request.setBrowseNodeLookupRequest(bnlr);

        ItemLookupRequest ilr = objFactory1.createItemLookupRequest();
        ilr.setCondition("condition");
        ilr.setDeliveryMethod("deliveryDate");
        ilr.setFutureLaunchDate("futureLaunchDate");
        ilr.setIdType("idType");
        ilr.setISPUPostalCode("ispuPostalCode");
        ilr.setMerchantId("merchantId");
        ilr.setOfferPage(new BigInteger("103"));
        List<String> ilrl1 = ilr.getItemId();
        ilrl1.add("itemId");
        List<String> ilrRg = ilr.getResponseGroup();
        isrRg.add("rg1234");
        ilr.setReviewPage(new BigInteger("104"));
        ilr.setSearchIndex("searchIndex");
        ilr.setSearchInsideKeywords("searchInsideKeywords");
        ilr.setVariationPage(new BigInteger("105"));

        request.setItemLookupRequest(ilr);

        ItemSearchRequest isr = objFactory1.createItemSearchRequest();
        isr.setManufacturer("manufacturer");
        isr.setMaximumPrice(new BigInteger("101"));
        isr.setMerchantId("merchantId");
        isr.setMinimumPrice(new BigInteger("102"));
        isr.setMusicLabel("musicLabel");
        isr.setNeighborhood("neighborhood");
        isr.setOrchestra("orchestra");
        isr.setPostalCode("postalCode");
        isr.setPower("power");
        isr.setPublisher("publisher");
        List<String> isrRg = isr.getResponseGroup();
        isrRg.add("rg123");
        isr.setSearchIndex("searchIndex");
        isr.setSort("sort");
        isr.setState("state");
        isr.setTextStream("textStream");
        isr.setTitle("title");

        request.setItemSearchRequest(isr);

        ListSearchRequest lsr = objFactory1.createListSearchRequest();
        lsr.setCity("Austin");
        lsr.setEmail("webservices@us.ibm.com");
        lsr.setFirstName("Web");
        lsr.setLastName("Services");
        lsr.setListPage(new BigInteger("106"));
        lsr.setListType("listType");
        lsr.setName("name");
        List<String> lsrRg = lsr.getResponseGroup();
        lsrRg.add("rg12345");
        lsr.setState("TX");

        request.setListSearchRequest(lsr);

        ListLookupRequest llr = objFactory1.createListLookupRequest();
        llr.setCondition("condition");
        llr.setDeliveryMethod("deliveryMethod");
        llr.setISPUPostalCode("postalCode");
        llr.setListId("listId");
        llr.setListType("listType");
        llr.setMerchantId("merchantId");
        llr.setProductGroup("productGroup");
        llr.setProductPage(new BigInteger("107"));
        List<String> llrRg = llr.getResponseGroup();
        llrRg.add("rg123456");
        llr.setSort("sort");

        request.setListLookupRequest(llr);

        CustomerContentSearchRequest ccsr = objFactory1.createCustomerContentSearchRequest();
        ccsr.setCustomerPage(new BigInteger("108"));
        ccsr.setEmail("email");
        ccsr.setName("name");
        List<String> ccsrRg = ccsr.getResponseGroup();
        ccsrRg.add("rg1234567");

        request.setCustomerContentSearchRequest(ccsr);

        CustomerContentLookupRequest cclr = objFactory1.createCustomerContentLookupRequest();
        cclr.setCustomerId("customerId");
        List<String> cclrRg = cclr.getResponseGroup();
        cclrRg.add("rg12345678");
        cclr.setReviewPage(new BigInteger("109"));

        request.setCustomerContentLookupRequest(cclr);

        SimilarityLookupRequest slr = objFactory1.createSimilarityLookupRequest();
        slr.setCondition("condition");
        slr.setDeliveryMethod("deliveryMethod");
        List<String> slrId = slr.getItemId();
        slr.setISPUPostalCode("postalCode");
        slr.setMerchantId("merchantId");
        List<String> slrRg = slr.getResponseGroup();
        slrRg.add("rg123456789");
        slr.setSimilarityType("similarityType");

        request.setSimilarityLookupRequest(slr);

        CartGetRequest cgr = objFactory1.createCartGetRequest();
        cgr.setCartId("cartId");
        cgr.setHMAC("HMAC");
        cgr.setMergeCart("mergeCart");
        List<String> cgrRg = cgr.getResponseGroup();
        cgrRg.add("rg12345678910");

        request.setCartGetRequest(cgr);

        CartAddRequest car = objFactory1.createCartAddRequest();
        car.setCartId("cartId");
        car.setHMAC("HMAC");
        car.setMergeCart("mergeCart");
        List<String> carRg = car.getResponseGroup();
        carRg.add("rg12345678910");

        request.setCartAddRequest(car);

        CartCreateRequest ccr = objFactory1.createCartCreateRequest();
        ccr.setMergeCart("setMergeCart");
        List<String> ccrRg = ccr.getResponseGroup();
        ccrRg.add("listing1");
        ccrRg.add("listing2");

        CartCreateRequest.Items ccrItem = objFactory1.createCartCreateRequestItems();
        
        List<CartCreateRequest.Items.Item> ccrLItem = ccrItem.getItem();

        CartCreateRequest.Items.Item ccrItems = objFactory1.createCartCreateRequestItemsItem();

        ccrItems.setASIN("ASIN");
        ccrItems.setOfferListingId("OfferListingId");
        ccrItems.setQuantity(new BigInteger("2323"));
        ccrItems.setAssociateTag("AssociateTag");
        ccrItems.setListItemId("ListItemId");
        
        ccrLItem.add(ccrItems);
        
        ccr.setItems(ccrItem);

        request.setCartCreateRequest(ccr);

        CartModifyRequest cmr = objFactory1.createCartModifyRequest();
        cmr.setCartId("CartId");
        cmr.setHMAC("HMAC");
        cmr.setMergeCart("MergeCart");
        
        CartModifyRequest.Items cmrItem = objFactory1.createCartModifyRequestItems();
        
        List<CartModifyRequest.Items.Item> cmrLItem = cmrItem.getItem();

        CartModifyRequest.Items.Item cmrItems = objFactory1.createCartModifyRequestItemsItem();

        cmrItems.setAction("action");
        cmrItems.setCartItemId("CartItemId");
        cmrItems.setQuantity(new BigInteger("2342"));
        
        cmrLItem.add(cmrItems);

        cmr.setItems(cmrItem);

        List<String> cmrRg = cmr.getResponseGroup();
        cmrRg.add("listing1");
        cmrRg.add("listing2");

        request.setCartModifyRequest(cmr);

        CartClearRequest ccr2 = objFactory1.createCartClearRequest();
        ccr2.setCartId("CartId");
        ccr2.setHMAC("HMAC");
        ccr2.setMergeCart("MergeCart");
        List<String> ccrrRg = ccr2.getResponseGroup();
        ccrrRg.add("listing1");
        ccrrRg.add("listing2");

        request.setCartClearRequest(ccr2);

        TransactionLookupRequest tlr = objFactory1.createTransactionLookupRequest();
        List<String> tlrRg = tlr.getResponseGroup();
        tlrRg.add("listing1");
        tlrRg.add("listing2");
        List<String> tlrTId = tlr.getTransactionId();
        tlrTId.add("transactionID1");
        tlrTId.add("transactionID2");

        request.setTransactionLookupRequest(tlr);

        SellerListingSearchRequest slsr = objFactory1.createSellerListingSearchRequest();
        slsr.setBrowseNode("BrowseNode");
        slsr.setCountry("Country");
        slsr.setKeywords("Keywords");
        slsr.setListingPage(new BigInteger("121"));
        slsr.setOfferStatus("OfferStatus");
        slsr.setPostalCode("PostalCode");
        List<String> slsrRg = slsr.getResponseGroup();
        slsrRg.add("listing1");
        slsrRg.add("listing2");
        slsr.setSearchIndex("SearchIndex");
        slsr.setSellerId("SellerId");
        slsr.setShipOption("ShipOption");
        slsr.setSort("Sort");
        slsr.setTitle("Title");

        request.setSellerListingSearchRequest(slsr);

        SellerListingLookupRequest sllr = objFactory1.createSellerListingLookupRequest();
        sllr.setId("id");
        sllr.setIdType("IdType");
        List<String> sllrRg = sllr.getResponseGroup();
        sllrRg.add("listing1");
        sllrRg.add("listing2");

        request.setSellerListingLookupRequest(sllr);

        SellerLookupRequest slr2 = objFactory1.createSellerLookupRequest();
        List<String> slrRg2 = slr2.getResponseGroup();
        slrRg2.add("listing1");
        slrRg2.add("listing2");
        List<String> slr2Id = slr2.getSellerId();
        slr2Id.add("id1");
        slr2Id.add("id2");
        slr2.setFeedbackPage(new BigInteger("1223"));

        request.setSellerLookupRequest(slr2);

        Errors errors = objFactory1.createErrors();
        List<Errors.Error> lErrors = errors.getError();

        Errors.Error e = objFactory1.createErrorsError();
        e.setCode("code");
        e.setMessage("message");

        lErrors.add(e);

        request.setErrors(errors);

        itms.setRequest(request);
        
        SearchResultsMap srm = objFactory1.createSearchResultsMap();
        srm.setIndexName("IndexName");
        srm.setResults(new BigInteger("100"));
        srm.setPages(new BigInteger("200"));
        srm.setRelevanceRank("1");
        List<String> lAsin = srm.getASIN();
        lAsin.add("asin1");
        lAsin.add("asin2");

        itms.setSearchResultsMap(srm);

        List<Item> lItm = itms.getItem();

        Item itm = objFactory1.createItem();

        itm.setASIN("ASIN");

        Errors errors2 = objFactory1.createErrors();
        List<Errors.Error> lErrors2 = errors2.getError();

        Errors.Error e2 = objFactory1.createErrorsError();
        e2.setCode("code");
        e2.setMessage("message");

        lErrors2.add(e2);

        itm.setErrors(errors2);
        itm.setDetailPageURL("DetailPageURL");
        itm.setSalesRank("SalesRank");
        
        Image img = objFactory1.createImage();
        img.setURL("URL");
        img.setIsVerified("Verified");

        DecimalWithUnits dwu = objFactory1.createDecimalWithUnits();
        dwu.setValue(new BigDecimal("1"));
        dwu.setUnits("units");
        
        img.setHeight(dwu);
        img.setWidth(dwu);

        itm.setSmallImage(img);
        itm.setMediumImage(img);
        itm.setLargeImage(img);
        */

    }

}
