// StepCount/IngrCount is gathered from the dom after the page loads since it comes from a th:list value
// The reason for the if/else statements for the counts - is for the off chance that the page loads a recipe without the lists
let stepCount;
if (document.getElementsByClassName("instruction").length > 0){ 
  stepCount = document.getElementById('stepCount').value;
} else {
  stepCount = 0;
  document.getElementById('stepCount').value = stepCount;
}
let ingrCount;
if (document.getElementsByClassName("ingredient").length > 0){ 
  ingrCount = document.getElementById('ingrCount').value;
} else {
  ingrCount = 0;
  document.getElementById('ingrCount').value = ingrCount;
}
let totalTime = 0;
var sizeArray = ["Size (Optional)","Teaspoons","Tablespoons","Cups","Ounces","Pints","Quarts","Gallons"];
// This section below is for everything related to updating and repainting the Total Time when prep and cook are updated
const prepBox = document.getElementById("prepTime");
const cookBox = document.getElementById("cookTime");

function calcTotal() {
  let newPrep = document.getElementById("prepTime");
  let newCook = document.getElementById("cookTime");
  // If-Else block checks if value of input box is empty, if so then update total time to the non-empty value or 0 if both empty
  if (newPrep.value === "" && newCook.value === "") {
    totalTime = 0;
  } else if (newPrep.value === "") {
    totalTime = newCook.value;
  } else if (newCook.value === "") {
    totalTime = newPrep.value;
  } else {
    totalTime = parseInt(newPrep.value) + parseInt(newCook.value);
  }
}
function paintTotal() {
  // first line is to remove the old element to be replaced with the updated time element
  document.getElementById("total-input").remove();
  let newTotal = document.createElement("input");
  newTotal.disabled = true;
  newTotal.setAttribute("class","form-time");
  newTotal.setAttribute("id","total-input")
  newTotal.value = totalTime;

  let totalDiv = document.getElementById("totalTime");
  totalDiv.append(newTotal);
}
function updateTotalTime() {
  calcTotal();
  paintTotal();
}

prepBox.addEventListener("input", updateTotalTime);
cookBox.addEventListener("input", updateTotalTime);

// These 3 are run for the initial page load to set value for recipes existing times and to load the delete ingredient and instruction buttons
calcTotal();
paintTotal();
paintDeleteSteps();
paintDeleteIngr();
// End of Total Time section


// INSTRUCTION STEP SECTION BEGINS
function addStep() {
  stepCount++;
  document.getElementById("stepCount").value = stepCount;
  const instructions = document.querySelector(".instructions");

  let newDiv = document.createElement("div");
  newDiv.setAttribute("class", "instruction border-bottom");
  newDiv.setAttribute("id", `step-${stepCount}`);

  let newLabel = document.createElement("label");
  newLabel.setAttribute("class", "form-label");
  newLabel.setAttribute("name", "step-label");

  let newSpan = document.createElement("span");
  newSpan.setAttribute("class", "step-label-span");
  newSpan.textContent = `Enter instructions for Step ${stepCount}`;

  let newTextArea = document.createElement("textarea");
  newTextArea.setAttribute("id", `instructionList${stepCount-1}.stepText`);
  newTextArea.setAttribute("class", "form-desc spacer-bottom stepText");
  newTextArea.setAttribute("name", `instructionList[${stepCount-1}].stepText`);
  newTextArea.required = true;

  newLabel.appendChild(newSpan);
  newLabel.appendChild(newTextArea);
  newDiv.appendChild(newLabel);

  let deleteStepBtnElement = deleteStepBtn();
  newDiv.appendChild(deleteStepBtnElement);
  
  instructions.appendChild(newDiv);
  return;
}
// This is important for adding the delete steps buttons to all the instruction elements - vital block
function paintDeleteSteps() {
  let instructionList = document.getElementsByClassName("instruction");
  for (let i = 1; i < instructionList.length; i++) {
    const deleteBtn = deleteStepBtn();
    instructionList[i].appendChild(deleteBtn); 
  }
}

function paintDeleteIngr() {
  let ingredientList = document.getElementsByClassName("ingredient");
  for (let i = 1; i < ingredientList.length; i++) {
    const deleteBtn = deleteIngrBtn();
    ingredientList[i].appendChild(deleteBtn); 
  }
}

function createStepBtn() {
  const addStepBtn = document.createElement("button");
  addStepBtn.setAttribute("type", "button");
  addStepBtn.setAttribute("class", "add-instr");
  addStepBtn.setAttribute("id", "addStepBtn");
  addStepBtn.textContent = "Add a Step";

  addStepBtn.addEventListener("click", (event) => {
    addStep();
  });

  return addStepBtn;
}

// This function houses the logic for the delete button - this is for the JS created elements
function deleteStepBtn() {
  const deleteBtn = document.createElement("button");
  deleteBtn.setAttribute("class", "rmv-instr bottom-gap")
  deleteBtn.setAttribute("type", "button");
  deleteBtn.setAttribute("id", "dlt-btn");
  deleteBtn.textContent = "Remove Step";

  deleteBtn.addEventListener("click", (event) => {
    stepCount--;
    document.getElementById("stepCount").value = stepCount;
    deleteBtn.parentElement.remove()
    repaintSteps();
  });
  return deleteBtn;
}
/* This is needed for when a ingredient is removed, this will update the values to stay linear
   The i+1 for id is needed since we aren't working with 0 being the starting value.
   All of the lines that change the Names of the input fields are required or else any removed instructions will
   come through to the backend as a null instruction object and cause an error. Doing it this way keeps the objects
   in the array correct */
function repaintSteps() {
  let instrList = document.getElementsByClassName("instruction");
  let instrInputs = document.getElementsByClassName("stepText");
  let instrLabels = document.getElementsByClassName("step-label-span");
  for (let i = 0; i < instrList.length; i++) {
    instrList[i].setAttribute("id", "step-"+(i+1));
    instrInputs[i].setAttribute("id","instructionList" +i+ ".stepText");
    instrInputs[i].setAttribute("name","instructionList[" +i+ "].stepText");
    instrLabels[i].textContent = "Enter instructions for Step " + (i+1);
  }
}

// INGREDIENT SECTION BEGINS
function addIngr() {
  ingrCount++;
  document.getElementById("ingrCount").value = ingrCount;
  const ingredients = document.querySelector(".ingredients");

  let newDiv = document.createElement("div");
  newDiv.setAttribute("class", "ingredient border-bottom");
  newDiv.setAttribute("id", `ingr-${ingrCount}`);

  let nameLabel = document.createElement("label");
  nameLabel.setAttribute("class", "form-ingr-label spacer");
  nameLabel.textContent = `Name`;

  let name = document.createElement("input");
  name.setAttribute("type", "text");
  name.setAttribute("name", `ingredientList[${ingrCount-1}].name`);
  name.setAttribute("class", "form-ingredient-name");
  name.setAttribute("placeholder", "Ingredient Name");
  name.required = true;

/* Child div is for using the flexbox settings for the 3 elements for quantity, sizing and prep info
   - they append to child and child appends to newDiv */
  let childDiv = document.createElement("div");
  childDiv.setAttribute("class", "form-ingredient-multi");

  let qtyDiv = document.createElement("div");
  let sizeDiv = document.createElement("div");
  let prepDiv = document.createElement("div");
  qtyDiv.setAttribute("class","form-ingredient-qty-div");
  sizeDiv.setAttribute("class","form-ingredient-size-div");
  prepDiv.setAttribute("class","form-ingredient-prep-div")

let qtyLabel = document.createElement("label");
  qtyLabel.setAttribute("class","form-ingr-label");
  qtyLabel.textContent = `Quantity`

  let sizeLabel = document.createElement("label");
  sizeLabel.setAttribute("class","form-ingr-label");
  sizeLabel.textContent = `Size (Optional)`;

  let prepLabel = document.createElement("label");
  prepLabel.setAttribute("class","form-ingr-label");
  prepLabel.textContent = `Prep Info (Optional)`;

  let qty = document.createElement("input");
  qty.setAttribute("type", "text");
  qty.setAttribute("name", `ingredientList[${ingrCount-1}].quantity`);
  qty.setAttribute("class", "form-ingredient-qty");
  qty.setAttribute("placeholder", "Qty");
  qty.required = true;

  let size = document.createElement("select");
  size.setAttribute("name", `ingredientList[${ingrCount-1}].size`);
  size.setAttribute("class", "form-ingredient-size");
  // Loops through the size array @ top of file and creates options.
  for (var i = 0; i < sizeArray.length; i++) {
    var option = document.createElement("option");
    if (sizeArray[i] == "Size (Optional)") {
      option.selected = true;
      option.value = "None";
      option.text = sizeArray[i];
      size.appendChild(option);
    } else {
      option.value = sizeArray[i];
      option.text = sizeArray[i];
      size.appendChild(option);
    }
  }

  let prep = document.createElement("input");
  prep.setAttribute("type", "text");
  prep.setAttribute("name", `ingredientList[${ingrCount-1}].prepType`);
  prep.setAttribute("class", "form-ingredient-prep");
  prep.setAttribute("placeholder", "(ex: minced, diced...)");

  nameLabel.appendChild(name);
  newDiv.appendChild(nameLabel);
  newDiv.appendChild(childDiv);

  qtyLabel.appendChild(qty);
  sizeLabel.appendChild(size);
  prepLabel.appendChild(prep);

  qtyDiv.appendChild(qtyLabel);
  sizeDiv.appendChild(sizeLabel);
  prepDiv.appendChild(prepLabel);

  childDiv.appendChild(qtyDiv);
  childDiv.appendChild(sizeDiv);
  childDiv.appendChild(prepDiv);

  let removeIngrBtn = deleteIngrBtn();
  newDiv.appendChild(removeIngrBtn);

  ingredients.appendChild(newDiv);
  return;
}

function createIngrBtn() {
  const addIngrBtn = document.createElement("button");
  addIngrBtn.setAttribute("class","add-ingr");
  addIngrBtn.setAttribute("id", "addIngrBtn");
  addIngrBtn.setAttribute("type", "button");
  addIngrBtn.textContent = "Add Ingredient";

  addIngrBtn.addEventListener("click", (event) => {
    addIngr();
  });

  return addIngrBtn;
}

function deleteIngrBtn() {
  const deleteIngrBtn = document.createElement("button");
  deleteIngrBtn.setAttribute("class", "rmv-ingr bottom-gap")
  deleteIngrBtn.setAttribute("type", "button");
  deleteIngrBtn.textContent = "Remove Ingredient";

  deleteIngrBtn.addEventListener("click", (event) => {
    ingrCount--;
    document.getElementById("ingrCount").value = ingrCount;
    deleteIngrBtn.parentElement.remove();
    repaintIngrIds();
  });
  return deleteIngrBtn;
}
/* This is needed for when a ingredient is removed, this will update the values to stay linear
   The i+1 for id is needed since we aren't working with 0 being the starting value.
   All of the lines that change the ID and Names of the input fields are required or else any removed ingredients will
   come through to the backend as a null ingredient object and cause an error. Doing it this way keeps the objects
   in the array correct */
function repaintIngrIds(){
  let ingrList = document.getElementsByClassName("ingredient");
  let nameInputs = document.getElementsByClassName("form-ingredient-name");
  let qtyInputs = document.getElementsByClassName("form-ingredient-qty");
  let sizeInputs = document.getElementsByClassName("form-ingredient-size");
  let prepInputs = document.getElementsByClassName("form-ingredient-prep");
  for (let i=0; i < ingrList.length; i++) {
    ingrList[i].setAttribute("id", "ingr-"+(i+1));
    nameInputs[i].setAttribute("id", "ingredientList" +i+ ".name");
    nameInputs[i].setAttribute("name", "ingredientList[" +i+ "].name");
    qtyInputs[i].setAttribute("id", "ingredientList" +i+ ".quantity");
    qtyInputs[i].setAttribute("name", "ingredientList[" +i+ "].quantity");
    sizeInputs[i].setAttribute("id", "ingredientList" +i+ ".size");
    sizeInputs[i].setAttribute("name", "ingredientList[" +i+ "].size");
    prepInputs[i].setAttribute("id", "ingredientList" +i+ ".prepType");
    prepInputs[i].setAttribute("name", "ingredientList[" +i+ "].prepType");
  }
}

// THIS IS FOR HANDLING THE IMAGE UPLOAD SECTION
function removeImage() {
  const oldDiv = document.getElementById("imageBody");
  oldDiv.remove();

  const imageDiv = document.getElementById("imageDiv");

  let newDiv = document.createElement("div");
  newDiv.setAttribute("class", "no-image");
  newDiv.setAttribute("id", "noImageDiv");

  let newInput = document.createElement("input");
  newInput.setAttribute("type","file");
  newInput.setAttribute("name","image");
  newInput.setAttribute("accept","image/*");
  newInput.setAttribute("id","image");

  let hiddenInput = document.createElement("input");
  hiddenInput.setAttribute("hidden", true);
  hiddenInput.setAttribute("name","deleted");
  
  newDiv.appendChild(newInput);
  newDiv.appendChild(hiddenInput);
  imageDiv.appendChild(newDiv);
}



