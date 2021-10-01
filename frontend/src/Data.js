import rawData from './update4'

const getClasses = (nativeClass) => {
    for(var i =0; i < rawData.length; i++){
        if(nativeClass === rawData[i]['NativeClass'])
            return rawData[i]['Classes']
    }
    return []
}
const items = getClasses("Class'/Script/FactoryGame.FGItemDescriptor'").concat(
        getClasses("Class'/Script/FactoryGame.FGResourceDescriptor'")).concat(
        getClasses("Class'/Script/FactoryGame.FGItemDescriptorNuclearFuel'")).concat(
        getClasses("Class'/Script/FactoryGame.FGConsumableEquipment'")).concat(
        getClasses("Class'/Script/FactoryGame.FGItemDescriptorBiomass'"))


const buildings = getClasses("Class'/Script/FactoryGame.FGBuildableManufacturer'")

const recipes = getClasses("Class'/Script/FactoryGame.FGRecipe'")



export {items, buildings, recipes}